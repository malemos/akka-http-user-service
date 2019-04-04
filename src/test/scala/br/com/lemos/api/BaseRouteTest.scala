package br.com.lemos.api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import br.com.lemos.api.model.{UserCreatedResponse, UserRequest, UserResponse, ErrorResponse}
import br.com.lemos.controller.UserController
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, OneInstancePerTest, WordSpec}

import scala.concurrent.ExecutionContext

trait BaseRouteTest
    extends WordSpec
    with MustMatchers
    with ScalaFutures
    with ScalatestRouteTest
    with MockFactory
    with OneInstancePerTest
    with JsonSupport {

  implicit val UserCreatedResponseDecoder: Decoder[UserCreatedResponse] = deriveDecoder
  implicit val UserRequestEncoder: Encoder[UserRequest] = deriveEncoder
  implicit val UserResponseDecoder: Decoder[UserResponse] = deriveDecoder
  implicit val errorResponseDecoder: Decoder[ErrorResponse] = deriveDecoder

  val userControllerMock: UserController = mock[UserController]

  private val api = new Api {
    override implicit val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
    override val userController: UserController = userControllerMock
  }

  val routes: Route = api.routes

}
