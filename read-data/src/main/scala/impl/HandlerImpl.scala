package impl

import akka.actor.typed.ActorSystem
import akka.stream.scaladsl.{Keep, Sink, Source, SourceQueueWithComplete}
import akka.stream.{OverflowStrategy, QueueOfferResult}
import api.Result.{Accepted, Rejected}
import api.{Address, Client, Handler, Payload}

import scala.concurrent.Future
import scala.concurrent.duration.Duration

class HandlerImpl(client: Client, val timeout: Duration)(implicit system: ActorSystem[_]) extends Handler {

  implicit val ec = system.executionContext
  override def performOperation(): Unit = {
    lazy val queue: SourceQueueWithComplete[(Address, Payload)] = Source
      .queue[(Address, Payload)](10, OverflowStrategy.backpressure)
      .mapAsync(16) { case (address, payload) =>
        Future(client.sendData(address, payload))
          .map {
            case Rejected => Some(address, payload)
            case Accepted => None
          }
      }
      .toMat(Sink.foreach { r =>
        r.getOrElse(recover(_, _))
        println(r)
      })(Keep.left)
      .run()

    def recover(address: Address, payload: Payload): Future[QueueOfferResult] = {
      Thread.sleep(timeout.toMillis)
      queue.offer(address -> payload)
    }

    Source
      .repeat(client.readData())
      .mapAsync(1) { event =>
        Source(event.recipients).mapAsync(16) { address =>
          queue.offer(address -> event.payload)
        }.run()
      }.run()
  }
}
