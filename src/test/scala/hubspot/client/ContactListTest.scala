package hubspot.client

import hubspot.api.{ContactList, HubspotClient}
import hubspot.domain.HContactList
import org.junit.Test

/**
 * Created by phuonglam on 2/14/17.
 **/
class ContactListTest {
  val hubspot = new HubspotClient(apiKey = Some("demo"), isDebug = true) with ContactList

  @Test
  def testCRUD(): Unit = {
    val resCreate = hubspot.ContactList.create(HContactList(
      name = "Test name 1"
    ))
    assert(resCreate.isNotError)
  }
}
