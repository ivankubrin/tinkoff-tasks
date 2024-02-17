package api

import scala.concurrent.duration.Duration

trait Handler {
  def timeout(): Duration

  def performOperation(): Unit
}
