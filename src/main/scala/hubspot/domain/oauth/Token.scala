package hubspot.domain.oauth

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author sonpn
 */
case class Token(
  @JsonProperty("refresh_token") refreshToken: String,
  @JsonProperty("access_token") accessToken: String,
  @JsonProperty("expires_in") expiresIn: Int
)
