package hubspot.domain

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by phuonglam on 2/13/17.
 **/
case class HResponse[A](
  code: Int = 200,
  error: Option[HError] = None,
  data: Option[A] = None
) {
  def isCodeInRange(lower: Int, upper: Int): Boolean = lower <= code && code <= upper

  def isConflict: Boolean = code == 409

  def is2xx: Boolean = isCodeInRange(200, 299)

  def isSuccess: Boolean = is2xx

  def is3xx: Boolean = isCodeInRange(300, 399)

  def isRedirect: Boolean = is3xx

  def is4xx: Boolean = isCodeInRange(400, 499)

  def isClientError: Boolean = is4xx

  def is5xx: Boolean = isCodeInRange(500, 599)

  def isServerError: Boolean = is5xx

  def isError: Boolean = is4xx || is5xx

  def isNotError: Boolean = !isError
}

case class HError(
  status: String,
  message: String,
  @JsonProperty("correlationId") correlationId: String,
  @JsonProperty("requestId") requestId: String,
  @JsonProperty("validationResults") validationResults: Seq[ValidationResult] = Seq[ValidationResult](),
  error: String = "UNKNOWN_ERROR",
  vid: Long = 0L
)

case class ValidationResult(
  @JsonProperty("isValid") isValid: Boolean,
  message: String,
  error: String,
  name: String
)