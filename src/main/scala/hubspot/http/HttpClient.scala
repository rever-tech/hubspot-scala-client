package hubspot.http

import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import hubspot.domain._
import hubspot.domain.oauth.{AccessToken, OAuthConfig, RefreshToken, Token}
import hubspot.exception.{InvalidRefreshToken, RefreshTokenException}
import hubspot.util.JsonUtils

import scalaj.http.{Http, HttpRequest, HttpResponse}

/**
 * Created by phuonglam on 2/14/17.
 **/
abstract class HttpClient {

  def makeHttpRequest(path: String): HttpRequest

  def isDebug: Boolean

  def GET[A: Manifest](path: String): HResponse[A] = {
    val req = makeHttpRequest(path)
    val res = req.asString
    printRequest(req, res)
    extract[A](res)
  }

  def POST[A: Manifest](path: String, data: String): HResponse[A] = {
    val req = makeHttpRequest(path).postData(data)
    val res = req.asString
    printRequest(req, res, data)
    extract[A](res)
  }

  def PUT[A: Manifest](path: String, data: String): HResponse[A] = {
    val req = makeHttpRequest(path).put(data)
    val res = req.asString
    printRequest(req, res, data)
    extract[A](res)
  }

  def DELETE[A: Manifest](path: String): HResponse[A] = {
    val req = makeHttpRequest(path).method("DELETE")
    val res = req.asString
    printRequest(req, res)
    extract[A](res)
  }

  protected def extract[A: Manifest](res: HttpResponse[String]) = {
    HResponse(
      code = res.code,
      data = if (res.isNotError && res.body.nonEmpty) {
        Some(JsonUtils.fromJson[A](res.body))
      } else None,
      error = if (res.isError) {
        Some(JsonUtils.fromJson[HError](res.body))
      } else None
    )
  }

  protected def printRequest(req: HttpRequest, res: HttpResponse[String], body: String = ""): Unit = {
    if (isDebug) {
      println("")
      println(s"______________________________________________")
      println(s"${req.method} ${req.url}")
      req.headers.foreach(f => {
        println(s"[Header]  ${f._1} -> ${f._2.toString}")
      })
      println(s"$body")
      println(s"----------------------------------------------")
      println(s"[Status]  ${res.code}")
      res.headers.foreach(f => {
        println(s"[Header]  ${f._1} -> ${f._2.toString}")
      })
      println(s"${res.body}")
    }
  }
}

case class APIKeyHttpClient(hapiKey: String, debug: Boolean = false) extends HttpClient {

  override def isDebug = debug

  override def makeHttpRequest(path: String): HttpRequest = {
    Http(s"$path${if (path.contains("?")) "&" else "?"}hapikey=$hapiKey")
  }
}

case class TokenHttpClient(oauthConfig: OAuthConfig, debug: Boolean = false) extends HttpClient {
  val tokenUri = "https://api.hubapi.com/oauth/v1/token"
  val accessTokenInfoUri = "https://api.hubapi.com/oauth/v1/access-tokens"
  val refreshTokenInfoUri = "https://api.hubapi.com/oauth/v1/refresh-tokens"

  val clientId = oauthConfig.clientId
  val clientSecret = oauthConfig.clientSecret

  val refreshToken = oauthConfig.refreshToken
  val accessToken = new AtomicReference[String](oauthConfig.accessToken match {
    case Some(x) => x
    case _ => null
  })

  val scheduler = new RefreshTokenScheduler {
    override def doRefreshToken(): Option[AccessToken] = {
      refreshAccessToken(tokenUri, clientId, clientSecret, refreshToken) match {
        case Some(x) =>
          println(s"New access token: ${x.token}, expireTime: ${x.expiresIn}")
          accessToken.set(x.token)
          Some(x)
        case _ => None
      }
    }

    override def notifyRefreshTokenFailed(msg: String): Unit = println(msg)
  }

  verifyToken()

  private[this] def verifyToken() = {
    if (refreshToken == null || refreshToken.isEmpty) throw new InvalidRefreshToken("refresh token is empty")
    if (getRefreshTokenInfo(refreshToken).isEmpty) throw new InvalidRefreshToken("refresh token is invalid")

    (if (accessToken.get() == null || accessToken.get().isEmpty) refreshAccessToken(tokenUri, clientId, clientSecret, refreshToken)
    else {
      val accessTokenInfo = getAccessTokenInfo(accessToken.get())
      if (accessTokenInfo.isEmpty) refreshAccessToken(tokenUri, clientId, clientSecret, refreshToken) else accessTokenInfo
    }) match {
      case Some(x) =>
        println(s"Access token: ${x.token}, expireTime: ${x.expiresIn}")
        accessToken.set(x.token)
        scheduler.setSleepTime(x.expiresIn)
        scheduler.start()
      case _ => throw new RefreshTokenException("Failed when refresh access token")
    }
  }

  override def makeHttpRequest(path: String): HttpRequest = {
    Http(path).header("Authorization", s"Bearer ${accessToken.get()}")
  }

  override def isDebug: Boolean = debug

  private def refreshAccessToken(tokenUri: String, clientId: String, clientSecret: String, refreshToken: String): Option[AccessToken] = {
    val resp = Http(tokenUri).timeout(5000, 10000)
      .method("POST")
      .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
      .param("grant_type", "refresh_token")
      .param("client_id", clientId)
      .param("client_secret", clientSecret)
      .param("refresh_token", refreshToken).asString
    resp.code match {
      case 200 =>
        val token = JsonUtils.fromJson[Token](resp.body)
        Some(AccessToken(token.accessToken, token.expiresIn))
      case _ => None
    }
  }

  private def getRefreshTokenInfo(refreshToken: String): Option[RefreshToken] = {
    val resp = Http(s"$refreshTokenInfoUri/$refreshToken").asString
    resp.code match {
      case 200 => Some(RefreshToken())
      case _ => None
    }
  }

  private def getAccessTokenInfo(accessToken: String): Option[AccessToken] = {
    val resp = Http(s"$accessTokenInfoUri/$accessToken").asString
    resp.code match {
      case 200 => Some(JsonUtils.fromJson[AccessToken](resp.body))
      case _ => None
    }
  }
}

abstract class RefreshTokenScheduler extends Thread {
  val isRunning = new AtomicBoolean(false)
  val deltaSleep: Int = 1 * 60 * 60
  var sleepTimeInSecond: Int = -1

  val sleepTimeWhenFailed: Long = 5000l
  val maxRetries: Int = 10
  var currentRetry: Int = 0

  override def run(): Unit = {
    while (isRunning.get()) {
      val timeSleep = (sleepTimeInSecond - deltaSleep) * 1000l
      if (timeSleep > 0) {
        Thread.sleep(timeSleep)
      }
      sleepTimeInSecond = 0
      try {
        doRefreshToken() match {
          case Some(x) => sleepTimeInSecond = x.expiresIn
          case _ => executeFailed("Failed when refresh access token")
        }
      } catch {
        case ex: Exception =>
          println(ex)
          executeFailed(s"Exception when refresh access token: ${ex.getMessage}")
      }
    }
  }

  def executeFailed(msg: String): Unit = {
    currentRetry = currentRetry + 1
    notifyRefreshTokenFailed(msg)
    Thread.sleep(sleepTimeWhenFailed)
    if (currentRetry > maxRetries) {
      notifyRefreshTokenFailed("Stop retry")
      stopSafe()
    }
  }

  def notifyRefreshTokenFailed(msg: String): Unit

  def doRefreshToken(): Option[AccessToken]

  override def start(): Unit = {
    isRunning.set(true)
    super.start()
  }

  def setSleepTime(time: Int): Unit = this.sleepTimeInSecond = time

  def stopSafe(): Unit = isRunning.set(false)
}