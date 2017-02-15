package hubspot.client

import com.fasterxml.jackson.databind.JsonNode
import hubspot.OAuthConfigForTest
import hubspot.api.HubspotClient

/**
 * @author sonpn
 */
object OAuthTest {
  def main(args: Array[String]): Unit = {
    val service = new HubspotClient(oauthConfig = Option(OAuthConfigForTest.apply))
    val contacts = service.client.GET[JsonNode]("https://api.hubapi.com/contacts/v1/lists/all/contacts/all")
    println(contacts)
    System.exit(0)
  }
}
