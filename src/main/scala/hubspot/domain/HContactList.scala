package hubspot.domain

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by phuonglam on 2/14/17.
 **/
case class HContactList(
  name: String,
  parentId: Long = 0,
  dynamic: Boolean = false,
  portalId: Long = 0,
  createdAt: Long = 0,
  listId: Long = 0,
  updatedAt: Long = 0,
  listType: String = "DYNAMIC",
  internalListId: Long = 0,
  deleteable: Boolean = true,
  @JsonProperty("metaData") metaData: Option[HContactListMetaData] = None,
  filters: Seq[Seq[HContactListFilter]] = Seq()
)

case class HContactListMetaData(
  processing: String,
  size: Int,
  error: String,
  @JsonProperty("lastProcessingStateChangeAt") lastProcessingStateChangeAt: Long,
  @JsonProperty("lastSizeChangeAt") lastSizeChangeAt: Long
)

case class HContactListFilter(
  withinTimeMode: String,
  checkPastVersions: Boolean,
  filterFamily: String,
  `type`: String,
  property: String,
  value: String,
  operator: String
)