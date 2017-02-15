package hubspot.domain

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by phuonglam on 2/9/17.
 **/
case class HContact(
  vid: Long,
  canonicalVid: Long,
  portalId: Long,
  isContact: Boolean,
  @JsonProperty("addedAt") addedAt: Long = 0L,
  profileToken: Option[String] = None,
  profileUrl: Option[String] = None,
  mergedVids: Seq[Long] = Seq[Long](),
  properties: Map[String, HContactProperties] = Map[String, HContactProperties](),
  formSubmissions: Seq[HFormSubmission] = Seq[HFormSubmission](),
  identityProfiles: Seq[HContactIdentityProfile] = Seq[HContactIdentityProfile](),
  mergeAudits: Seq[HContactMergeAudit] = Seq[HContactMergeAudit]()
) {
  def email: Option[String] = if (properties.contains("email")) {
    Option(properties("email").value)
  } else None
}

case class HFormSubmission(
  conversionId: String,
  timestamp: Long,
  formId: String,
  portalId: String,
  pageUrl: String,
  title: String
)

case class HContactMembership(
  staticListId: String,
  internalListId: String,
  timestamp: String,
  vid: Long,
  isMember: Boolean
)

case class HContactIdentityProfile(
  vid: Long,
  savedAtTimestamp: Long,
  deletedChangedTimestamp: Long,
  identities: Seq[HContactIdentity]
)

case class HContactIdentity(
  types: String,
  value: String,
  timestamp: Long
)

case class HContactProperties(
  value: String,
  property: String = "",
  versions: Seq[HObject] = Seq[HObject]()
)

case class HContactMergeAudit(
  canonicalVid: Long,
  vidToMerge: Long,
  timestamp: Long,
  entityId: String,
  userId: Long,
  numPropertiesMoved: Int,
  mergeFromEmail: HObject,
  mergeToEmail: HObject
)

