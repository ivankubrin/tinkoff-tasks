package api

trait Client {
  //блокирующий метод для чтения данных
  def readData(): Event

  //блокирующий метод отправки данных
  def sendData(dest: Address, payload: Payload): Result
}
