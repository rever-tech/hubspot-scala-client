package hubspot.api

import hubspot.domain._
import hubspot.util.JsonUtils._

/**
 * Created by phuonglam on 2/14/17.
 **/


trait Contact extends HubspotClient {

  object Contact {

    val baseUrl = s"$apiUrl/contacts/v1"

    def create(properties: Seq[HContactProperties]) = http.POST[HContact](
      s"$baseUrl/contact/",
      Map("properties" -> properties).toJsonString
    )

    def updateByVid(vid: Long, properties: Seq[HContactProperties]) = http.POST[HContactResponse](
      s"$baseUrl/contact/vid/$vid/profile",
      Map("properties" -> properties).toJsonString
    )

    def updateByEmail(email: String, properties: Seq[HContactProperties]) = http.POST[HContactResponse](
      s"$baseUrl/contact/email/$email/profile",
      Map("properties" -> properties).toJsonString
    )

    def createOrUpdate(email: String, properties: Seq[HContactProperties]) = http.POST[HContactCreateOrUpdateResponse](
      s"$baseUrl/contact/createOrUpdate/email/$email/",
      Map("properties" -> properties).toJsonString
    )

    def createOrUpdateGroup(request: Seq[HContactCreateOrUpdateRequest]) = http.POST[HContactResponse](
      s"$baseUrl/contact/batch/",
      request.toJsonString
    )

    def delete(vid: String) = http.DELETE[HContactDeleteResponse](s"$baseUrl/vid/$vid")

    private def _get[A: Manifest](
      getUrl: String,
      vidOffset: Option[Long] = None,
      property: Seq[String] = Seq[String](),
      propertyMode: HContactGetPropertyMode = HContactGetPropertyMode.default,
      formSubmissionMode: HContactFormSubmissionMode = HContactFormSubmissionMode.default,
      showListMemberships: Option[Boolean] = None
    ) = http.GET[A](
      s"$getUrl${
        vidOffset match {
          case Some(n) => s"&vidOffset=$n"
          case _ => ""
        }
      }${
        if (property.nonEmpty)
          s"&property=${property.mkString("&property=")}"
        else ""
      }${
        propertyMode.data match {
          case Some(s) => s"&propertyMode=$s"
          case _ => ""
        }
      }${
        formSubmissionMode.data match {
          case Some(s) => s"&formSubmissionMode=$s"
          case _ => ""
        }
      }${
        showListMemberships match {
          case Some(s) => s"&showListMemberships=$s"
          case _ => ""
        }
      }")

    def getAll(
      count: Int = 10,
      vidOffset: Option[Long] = None,
      property: Seq[String] = Seq[String](),
      propertyMode: HContactGetPropertyMode = HContactGetPropertyMode.default,
      formSubmissionMode: HContactFormSubmissionMode = HContactFormSubmissionMode.default,
      showListMemberships: Option[Boolean] = None
    ) = _get[HContactGetAllResponse](
      s"$baseUrl/lists/all/contacts/all?count=$count",
      vidOffset,
      property,
      propertyMode,
      formSubmissionMode,
      showListMemberships
    )

    def getRecent(
      count: Int = 10,
      vidOffset: Option[Long] = None,
      property: Seq[String] = Seq[String](),
      propertyMode: HContactGetPropertyMode = HContactGetPropertyMode.default,
      formSubmissionMode: HContactFormSubmissionMode = HContactFormSubmissionMode.default,
      showListMemberships: Option[Boolean] = None
    ) = _get[HContactGetAllResponse](
      s"$baseUrl/lists/recently_updated/contacts/recent?count=$count",
      vidOffset,
      property,
      propertyMode,
      formSubmissionMode,
      showListMemberships
    )

    def getByVid(vid: Long) = http.GET[HContact](s"$baseUrl/contact/vid/$vid/profile")

    def mgetByVid(
      vids: Seq[Long],
      property: Seq[String] = Seq[String](),
      propertyMode: HContactGetPropertyMode = HContactGetPropertyMode.default,
      formSubmissionMode: HContactFormSubmissionMode = HContactFormSubmissionMode.default,
      showListMemberships: Option[Boolean] = None,
      includeDeletes: Option[Boolean] = None
    ) = _get[Map[String, HContact]](s"$baseUrl/contact/vids/batch/?${
      s"vid=${vids.mkString("&vid=")}${
        includeDeletes match {
          case Some(s) => s"&includeDeletes=$s"
          case _ => ""
        }
      }"
    }", None, property, propertyMode, formSubmissionMode, showListMemberships)

    def getByEmail(email: String) = http.GET[HContact](s"$baseUrl/contact/email/$email/profile")

    def mgetByEmail(
      emails: Seq[String],
      property: Seq[String] = Seq[String](),
      propertyMode: HContactGetPropertyMode = HContactGetPropertyMode.default,
      formSubmissionMode: HContactFormSubmissionMode = HContactFormSubmissionMode.default,
      showListMemberships: Option[Boolean] = None,
      includeDeletes: Option[Boolean] = None
    ) = _get[Map[String, HContact]](s"$baseUrl/contact/vids/batch/?${
      s"email=${emails.mkString("&email=")}${
        includeDeletes match {
          case Some(s) => s"&includeDeletes=$s"
          case _ => ""
        }
      }"
    }", None, property, propertyMode, formSubmissionMode, showListMemberships)

    def search(query: String, count: Int = 10, offset: Option[Int] = None, property: Seq[String] = Seq[String]()) =
      _get[HContactSearchResponse](
        getUrl = s"$baseUrl/search/query?q=$query&count=$count${
          offset match {
            case Some(s) => s"&offset=$offset"
            case _ => ""
          }
        }", property = property

      )

    def merge(primaryVid: Long, secondaryVid: Long) = http.POST[HContactResponse](
      s"$baseUrl/contact/merge-vids/$primaryVid/",
      s"""
         |{
         | "vidToMerge": "$secondaryVid"
         |}
      """.stripMargin
    )
  }

}
