package org.bibliarij.akkahttpsample

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import org.bibliarij.akkahttpsample.web.Router
import org.bibliarij.akkahttpsample.web.Router.myExceptionHandler

import scala.concurrent.ExecutionContextExecutor

object Main {

  def main(args: Array[String]) {
    implicit val system: ActorSystem = ActorSystem("money-transfers-actor-system")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    Http().bindAndHandle(Router.route, "127.0.0.1", 8080)
    println(s"Server online at http://localhost:8080/")
  }
}
