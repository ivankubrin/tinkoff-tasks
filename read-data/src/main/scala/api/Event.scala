package api

final case class Event(recipients: List[Address], payload: Payload)
