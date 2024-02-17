package api

trait Client {
  //блокирующий вызов сервиса 1 для получения статуса заявки
  def getApplicationStatus1(id: String): Response


  //блокирующий вызов сервиса 2 для получения статуса заявки
  def  getApplicationStatus2(id: String): Response
}
