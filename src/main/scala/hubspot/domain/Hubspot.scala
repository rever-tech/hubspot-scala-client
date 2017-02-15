package hubspot.domain

/**
 * Created by phuonglam on 2/13/17.
 **/
case class Hubspot()

case class HObject(
  value: String,
  sourceType: String,
  sourceId: String,
  sourceLabel: String,
  sourceVids: Seq[Long],
  timestamp: Long
)
