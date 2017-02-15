package hubspot.domain

/**
 * Created by phuonglam on 2/13/17.
 **/

case class HContactResponse()

case class HContactDeleteResponse(
  vid: Long,
  deleted: Boolean,
  reason: String
)

case class HContactCreateOrUpdateResponse(
  vid: Long,
  isNew: Boolean
)

case class HContactGetAllResponse(
  contacts: Seq[HContact],
  hasMore: Boolean,
  vidOffset: Long,
  timeOffset: Long = 0L
)

case class HContactSearchResponse(
  contacts: Seq[HContact],
  offset: Long,
  hasMore: Boolean,
  total: Long
)