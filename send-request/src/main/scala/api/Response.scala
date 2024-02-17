package api

import scala.concurrent.duration.FiniteDuration

sealed trait Response

object Response {
  final case class Success(applicationStatus: String, applicationId: String) extends Response

  final case class RetryAfter(delay: FiniteDuration) extends Response

  final case class Failure(ex: Throwable) extends Response
}
