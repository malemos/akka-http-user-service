package br.com.lemos

import akka.actor.ActorSystem
import akka.event.slf4j.SLF4JLogging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorMaterializer, Materializer}
import br.com.lemos.api.Api
import br.com.lemos.config.AppConfig
import br.com.lemos.controller.UserControllerModule
import br.com.lemos.repository.RepositoryModule

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Microservice
    extends App
    with Api
    with RepositoryModule
    with UserControllerModule
    with AppConfig
    with SLF4JLogging {

  private implicit val system: ActorSystem = ActorSystem("AkkaHttpCatService")
  override implicit val executionContext: ExecutionContext = system.dispatcher
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  val serverBinding =
    Http().bindAndHandle(handler = logRequestResult("log")(routes), interface = serverInterface, port = serverPort)

  serverBinding.onComplete {
    case Success(_) ⇒
      log.info(s"Server started: {}:{}", serverInterface, serverPort)
    case Failure(e) ⇒
      log.error("Error during server binding: {}", e.getMessage)
  }

  sys.addShutdownHook {
    serverBinding
      .flatMap(_.unbind())
      .onComplete(_ ⇒ system.terminate())
  }
}
