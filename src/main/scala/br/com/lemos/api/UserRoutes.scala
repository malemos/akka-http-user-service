package br.com.lemos.api

import akka.http.scaladsl.model.RemoteAddress
import akka.http.scaladsl.model.headers.`User-Agent`
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{delete, get, post}
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import br.com.lemos.api.model.{UserRequest, RequestMetadata}
import br.com.lemos.controller.UserController
import kamon.akka.http.TracingDirectives

class UserRoutes(userController: UserController) extends JsonSupport with TracingDirectives {

  lazy val userRoutes: Route =
    pathPrefix("users") {
      pathEnd {
        get {
          operationName("get-users") {
            complete(userController.findAll.value)
          }
        } ~
          post {
            entity(as[UserRequest]) { userRequest =>
              optionalHeaderValueByType[`User-Agent`](()) { userAgent ⇒
                extractClientIP { remoteAddress ⇒
                  operationName("insert-user") {
                    val requestMetadata = prepareRequestMetadata(userAgent, remoteAddress)
                    complete(userController.create(userRequest, requestMetadata).value)
                  }
                }
              }
            }
          }
      } ~
        path(Segment) { id =>
          get {
            operationName("get-user-by-id") {
              complete(userController.getById(id).value)
            }
          } ~
            delete {
              operationName("delete-user") {
                complete(userController.delete(id).value)
              }
            }
        }
    }

  private def prepareRequestMetadata(userAgent: Option[`User-Agent`], remoteAddress: RemoteAddress) =
    RequestMetadata(
      userAgent = userAgent.map(_.value),
      clientIP = remoteAddress.toIP.map(_.ip.getHostAddress).getOrElse("unknown")
    )

}
