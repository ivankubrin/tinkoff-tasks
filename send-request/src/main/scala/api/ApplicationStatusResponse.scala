package api

import scala.concurrent.duration.Duration

sealed trait ApplicationStatusResponse

object ApplicationStatusResponse {
  final case class Failure(lastRequestTime: Option[Duration], retriesCount: Int) extends ApplicationStatusResponse

  final case class Success(id: String, status: String) extends ApplicationStatusResponse
}

