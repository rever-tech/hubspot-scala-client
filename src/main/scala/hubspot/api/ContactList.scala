package hubspot.api

import hubspot.domain.HContactList
import hubspot.util.JsonUtils._

/**
 * Created by phuonglam on 2/14/17.
 **/
trait ContactList extends HubspotClient {

  object ContactList {
    val baseUrl = s"$apiUrl/contacts/v1/lists"

    def create(contactList: HContactList) = http.POST[HContactList](
      s"$baseUrl", contactList.toJsonString
    )


  }

}
