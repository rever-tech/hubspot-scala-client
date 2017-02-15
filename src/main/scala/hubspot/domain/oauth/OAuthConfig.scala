package hubspot.domain.oauth

/**
 * @author sonpn
 */
case class OAuthConfig(clientId: String, clientSecret: String,refreshToken: String, accessToken: Option[String] = None)
