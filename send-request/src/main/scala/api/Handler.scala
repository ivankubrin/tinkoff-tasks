package api

trait Handler {
  def performOperation(id: String): ApplicationStatusResponse
}
