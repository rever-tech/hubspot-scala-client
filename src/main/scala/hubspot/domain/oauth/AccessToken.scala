package hubspot.domain.oauth

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author sonpn
 */
case class AccessToken(@JsonProperty("token") token: String, @JsonProperty("expires_in") expiresIn: Int)
