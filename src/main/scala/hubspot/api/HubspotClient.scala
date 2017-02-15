package hubspot.api

import hubspot.domain.oauth.OAuthConfig
import hubspot.http.{APIKeyHttpClient, HttpClient, TokenHttpClient}

/**
 * Created by phuonglam on 2/14/17.
 **/
class HubspotClient(oauthConfig: Option[OAuthConfig] = None, apiKey: Option[String] = None, isDebug: Boolean = false) {
  val apiUrl = s"https://api.hubapi.com"

  val client = {
    if (oauthConfig.isDefined) TokenHttpClient(oauthConfig.get, isDebug)
    else if (apiKey.isDefined) APIKeyHttpClient(apiKey.get, isDebug)
    else throw new Exception("HubspotClient must has one of oauthConfig or apiKey!")
  }

  def http: HttpClient = client
}
