package hubspot.domain

/**
 * Created by phuonglam on 2/13/17.
 **/
case class HContactCreateRequest(
  properties: Seq[HContactProperties]
)

case class HContactCreateOrUpdateRequest(
  vid: Option[Long] = None,
  email: Option[String] = None,
  properties: Seq[HContactProperties]
)

case class HContactGetPropertyMode(data: Option[String])
object HContactGetPropertyMode {
  val default = HContactGetPropertyMode(None)
  val valueOnly = HContactGetPropertyMode(Some("value_only"))
  val valueAndHistory = HContactGetPropertyMode(Some("value_and_history"))
}

case class HContactFormSubmissionMode(data: Option[String])
object HContactFormSubmissionMode {
  val default = HContactFormSubmissionMode(None)
  val all = HContactFormSubmissionMode(Some("all"))
  val none = HContactFormSubmissionMode(Some("none"))
  val newest = HContactFormSubmissionMode(Some("newest"))
}