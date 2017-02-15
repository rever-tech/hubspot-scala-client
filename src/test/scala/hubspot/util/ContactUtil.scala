package hubspot.util

import hubspot.domain.{HContactCreateOrUpdateRequest, HContactProperties}

/**
 * Created by phuonglam on 2/13/17.
 **/
object ContactUtil {

  def getEmail(cs: Seq[HContactProperties]): String = {
    for (c <- cs) if (c.property.equals("email")) return c.value
    ""
  }

  def genGroupContact = genGroupContacts(1)

  def genGroupContacts(count: Int): Seq[HContactCreateOrUpdateRequest] = {
    val contactProperties = genContact
    val email = getEmail(contactProperties)
    for (i <- 0 until count) yield HContactCreateOrUpdateRequest(
      email = Option(email),
      properties = contactProperties
    )
  }

  def genContact = {
    Seq(
      HContactProperties(
        property = "email",
        value = s"${Random.randomAlpha(10)}@gmail.com"
      ),
      HContactProperties(
        property = "firstName",
        value = Random.randomAlpha(5)
      ),
      HContactProperties(
        property = "lastName",
        value = Random.randomAlpha(7)
      )
    )
  }
}
