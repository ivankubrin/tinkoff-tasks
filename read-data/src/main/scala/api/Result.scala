package api

sealed trait Result

object Result {
  case object Accepted extends Result

  case object Rejected extends Result
}

