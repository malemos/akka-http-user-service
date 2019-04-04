package br.com.lemos.api

import java.net.InetAddress

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.`Remote-Address`
import br.com.lemos.api.model._
import br.com.lemos.controller.UserDeleted
import br.com.lemos.model._

class UserRoutesTest extends BaseRouteTest {

  "UserRoutes" when {
    "GET /users is requested" should {
      "return a list with users" in {
        // given
        val request = Get("/users")

        val userResponse = UserResponse("213213", "username", "teste@teste.com", "password")
        val controllerResult = AsyncResult.success(List(userResponse))
        (userControllerMock.findAll _).expects().returning(controllerResult)

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.OK
          contentType mustBe ContentTypes.`application/json`
          entityAs[List[UserResponse]] mustBe List(UserResponse("213213", "username", "teste@teste.com", "password"))
        }
      }

      "return an empty list when no users found" in {
        // given
        val request = Get("/users")

        val controllerResult = AsyncResult.success(List.empty[UserResponse])
        (userControllerMock.findAll _).expects().returning(controllerResult)

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.OK
          entityAs[List[UserResponse]] mustBe Nil
        }
      }

      "handle controller errors" in {
        // given
        val controllerResult = AsyncResult.failure[List[UserResponse]](InternalServiceError("DB not reachable."))
        (userControllerMock.findAll _).expects().returning(controllerResult)

        val request = Get("/users")

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.InternalServerError
          entityAs[ErrorResponse].message must not be empty
        }
      }
    }

    "GET /users/{id} is requested" should {

      "return a single user" in {
        // given
        val request = Get("/users/1")

        val user = UserResponse("213213", "username", "teste@teste.com", "password")
        val controllerResult = AsyncResult.success(user)
        (userControllerMock.getById _).expects("1").returning(controllerResult)

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.OK
          contentType mustBe ContentTypes.`application/json`
          entityAs[UserResponse] mustBe UserResponse("213213", "username", "teste@teste.com", "password")
        }
      }

      "return 404 when user not found" in {
        // given
        val request = Get("/users/1")

        val controllerResult = AsyncResult.failure[UserResponse](NotFoundError("User not found."))
        (userControllerMock.getById _).expects("1").returning(controllerResult)

        // when
        request ~> routes ~> check {

          // then
          entityAs[ErrorResponse].message must not be empty
          status mustBe StatusCodes.NotFound
          contentType mustBe ContentTypes.`application/json`
        }
      }

      "handle repository errors" in {
        // given
        val controllerResult = AsyncResult.failure[UserResponse](InternalServiceError("DB not reachable"))
        (userControllerMock.getById _).expects(*).returning(controllerResult)

        val request = Get("/users/1")

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.InternalServerError
          entityAs[ErrorResponse].message must not be empty
        }
      }
    }

    "POST /users is requested" should {

      "create an user" in {
        // given
        val request =
          Post("/users", UserRequest("username", "teste@teste.com", "password"))
            .withHeaders(`Remote-Address`(RemoteAddress(InetAddress.getByName("127.0.0.1"))))

        val userRequest = UserRequest("username", "teste@teste.com", "password")
        val requestMetadata = RequestMetadata(Some("curl"), "127.0.0.1")

        val controllerResult = AsyncResult.success(UserCreatedResponse("213213"))
        (userControllerMock.create _).expects(userRequest, requestMetadata).returning(controllerResult)

        // when
        request ~> addHeader("User-Agent", "curl") ~> routes ~> check {

          // then
          status mustBe StatusCodes.Created
          entityAs[UserCreatedResponse] mustBe UserCreatedResponse("213213")
        }
      }

      "handle wrong request body" in {
        // given
        val wrongRequestBody =
          """{"wrong": "body"}"""
        val request = Post("/users", HttpEntity(ContentTypes.`application/json`, wrongRequestBody))

        (userControllerMock.create _).expects(*, *).never()

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.BadRequest
          entityAs[ErrorResponse].message must not be empty
        }
      }

      "handle repository errors" in {
        // given
        val controllerResult = AsyncResult.failure[UserCreatedResponse](InternalServiceError("DB not reachable"))
        (userControllerMock.create _).expects(*, *).returning(controllerResult)

        val request = Post("/users", UserRequest("username", "teste@teste.com", "password"))

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.InternalServerError
          entityAs[ErrorResponse].message must not be empty
        }
      }
    }

    "DELETE /users is requested" should {

      "delete an user" in {
        // given
        val request = Delete("/users/1")

        val controllerResult = AsyncResult.success(UserDeleted)
        (userControllerMock.delete _).expects("1").returning(controllerResult)

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.NoContent
          responseAs[Option[String]] mustBe None
        }
      }

      "handle repository errors" in {
        // given
        val controllerResult = AsyncResult.failure[UserDeleted.type](InternalServiceError("DB not reachable"))
        (userControllerMock.delete _).expects("1").returning(controllerResult)

        val request = Delete("/users/1")

        // when
        request ~> routes ~> check {

          // then
          status mustBe StatusCodes.InternalServerError
          entityAs[ErrorResponse].message must not be empty
        }
      }

    }
  }
}
