package impl

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.scaladsl.Behaviors
import akka.pattern.StatusReply
import akka.util.Timeout
import api.{ApplicationStatusResponse, Client, Handler, Response}
import impl.HandlerImpl.{Command, GetCount, Recover, Success}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future, Promise}
import scala.util.Try

class HandlerImpl(client: Client)(implicit system: ActorSystem[Command]) extends Handler {

  val promise: Promise[ApplicationStatusResponse] = Promise[ApplicationStatusResponse]()

  def sendRequest(id: String, isService1: Boolean): Future[ApplicationStatusResponse] = {
    val response: Response =
      if (isService1)
        client.getApplicationStatus1(id)
      else
        client.getApplicationStatus2(id)

    response match {
      case response: Response.Success =>
        system ! Success(response, promise)
        promise.future
      case Response.RetryAfter(delay) =>
        system ! Recover
        Thread.sleep(delay.toMillis)
        sendRequest(id: String, isService1: Boolean)
      case Response.Failure(_) =>
        system ! Recover
        sendRequest(id: String, isService1: Boolean)
    }
  }

  override def performOperation(id: String): ApplicationStatusResponse = {
    sendRequest(id, true)
    sendRequest(id, false)

    Try(Await.result(promise.future, 15.seconds))
      .getOrElse {
        implicit val timeout: Timeout = 2.seconds
        val count: Int = Await.result(system.askWithStatus(GetCount), 1.seconds)
        ApplicationStatusResponse.Failure(None, count)
      }
  }
}

object HandlerImpl {
  sealed trait Command

  case class Success(response: Response.Success, promise: Promise[ApplicationStatusResponse]) extends Command

  case object Recover extends Command

  case class GetCount(replyTo: ActorRef[StatusReply[Int]]) extends Command

  def apply(): Behavior[Command] = Behaviors.setup[Command](_ => action())

  def action(failuresCount: Int = 0): Behaviors.Receive[Command] =
    Behaviors.receiveMessage[Command] {
      case Success(Response.Success(status, id), promise) =>
        if (!promise.isCompleted)
          promise.success(ApplicationStatusResponse.Success(id, status))
        Behaviors.same
      case Recover =>
        action(failuresCount + 1)
      case GetCount(replyTo) =>
        replyTo ! StatusReply.success(failuresCount)
        Behaviors.same
    }
}

