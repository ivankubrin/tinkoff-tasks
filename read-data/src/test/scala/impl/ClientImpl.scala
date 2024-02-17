package impl

import api.{Address, Client, Event, Payload, Result}

import scala.util.Random

object ClientImpl extends Client {

  val address1 = Address("test1", "test1")

  val address2 = Address("test2", "test2")

  def getPayload: Payload = {
    val n = Random.nextInt(10)
    Payload(n.toString, n.toString.getBytes)
  }


  def readData(): Event = Event(List(address1, address2), getPayload)

  def sendData(dest: Address, payload: Payload): Result =
    if (Random.nextInt(3) == 2)
      Result.Rejected
    else
      Result.Accepted
}
