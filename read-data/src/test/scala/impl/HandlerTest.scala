package impl

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration.DurationInt

class HandlerTest extends AnyWordSpec with Matchers {

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "test")

  val handler: HandlerImpl = new HandlerImpl(ClientImpl, 2.seconds)

  "test" in {
    handler.performOperation()

    Thread.sleep(10000)
  }
}
