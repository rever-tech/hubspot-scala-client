package hubspot

import hubspot.domain.oauth.OAuthConfig

/**
 * @author sonpn
 */
object OAuthConfigForTest {
  def apply = OAuthConfig(
    clientId = "",
    clientSecret = "",
    refreshToken = "",
    accessToken = None
  )
}