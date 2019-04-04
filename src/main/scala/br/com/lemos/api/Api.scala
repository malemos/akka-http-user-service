package br.com.lemos.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import br.com.lemos.controller.UserController

import scala.concurrent.ExecutionContext

trait Api extends ExceptionHandling with RejectionHandling with JsonSupport with Directives {

  implicit val executionContext: ExecutionContext

  val userController: UserController

  lazy val routes: Route =
    handleRequestTimeout {
      handleRejections(rejectionHandler) {
        handleExceptions(exceptionHandler) {
          new UserRoutes(userController).userRoutes
        }
      }
    }

}
