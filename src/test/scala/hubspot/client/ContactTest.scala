package hubspot.client

import hubspot.OAuthConfigForTest
import hubspot.api.{Contact, HubspotClient}
import hubspot.domain.HContactProperties
import hubspot.util.ContactUtil
import org.junit.Test

/**
 * Created by phuonglam on 2/13/17.
 **/
class ContactTest {
  val hubspot = new HubspotClient(apiKey = Some("demo"), oauthConfig = Some(OAuthConfigForTest.apply), isDebug = true) with Contact


  // create => update => get => delete
  @Test
  def testCRUD(): Unit = {
    val createRes = hubspot.Contact.create(ContactUtil.genContact)
    assert(createRes.isNotError, "create must response success")
    val createdContact = createRes.data.get
    assert(createdContact.vid > 0, "create must response vid")
    assert(createdContact.email.get.nonEmpty, "create must response email")

    val updateByVidData = ("name", "update value by vid")
    val updateByVidRes = hubspot.Contact.updateByVid(createdContact.vid, Seq(HContactProperties(
      property = updateByVidData._1,
      value = updateByVidData._2
    )))
    assert(updateByVidRes.isNotError, "updateByVid must success")

    val updateByEmailData = ("title", "update value by email")
    val updateByEmailRes = hubspot.Contact.updateByEmail(createdContact.email.get, Seq(HContactProperties(
      property = updateByEmailData._1,
      value = updateByEmailData._2
    )))
    assert(updateByEmailRes.isNotError, "updateByEmail must success")

    val getRes = hubspot.Contact.getByVid(createdContact.vid)
    assert(getRes.isNotError, "get response must success")

    val getContact = getRes.data.get
    assert(getContact.vid > 0, "get must response vid")
    assert(getContact.email.get.nonEmpty, "get must response email")

    assert(getContact.properties.contains(updateByVidData._1), "get data must contain updateFieldByVid field")
    assert(getContact.properties.contains(updateByEmailData._1), "get data must contain updateFieldByEmail field")

    assert(getContact.properties(updateByVidData._1).value.equals(updateByVidData._2), "get data must contain updateFieldByVid data")
    assert(getContact.properties(updateByEmailData._1).value.equals(updateByEmailData._2), "get data must contain updateFieldByEmail data")
  }

  @Test
  def testCreateOrUpdate(): Unit = {
    val genContact = ContactUtil.genContact
    val email = ContactUtil.getEmail(genContact)
    val createRes = hubspot.Contact.createOrUpdate(email, genContact)
    assert(createRes.isNotError)
    assert(createRes.data.get.vid > 0)

    val createRes1 = hubspot.Contact.createOrUpdate(email, genContact :+ HContactProperties(
      property = "name",
      value = "This is a new name"
    ))
    assert(createRes1.isNotError)
    assert(createRes1.data.get.vid > 0)
    assert(!createRes1.data.get.isNew)
  }

  @Test
  def testCreateOrUpdateGroup(): Unit = {
    val group = ContactUtil.genGroupContact
    val res = hubspot.Contact.createOrUpdateGroup(group)
    assert(res.isNotError)
  }

  @Test
  def testGetAll(): Unit = {
    val group = ContactUtil.genGroupContacts(20)
    val res1 = hubspot.Contact.createOrUpdateGroup(group)
    assert(res1.isNotError)

    val res2 = hubspot.Contact.getAll(10)
    assert(res2.isNotError)
    val all = res2.data.get
    assert(all.contacts.length == 10)
    assert(all.hasMore)
  }

  @Test
  def testGetRecent(): Unit = {
    val res2 = hubspot.Contact.getRecent(10)
    assert(res2.isNotError)
    val all = res2.data.get
    assert(all.contacts.length == 10)
    assert(all.hasMore)
  }
}