package br.com.lemos.controller

import akka.event.slf4j.SLF4JLogging
import br.com.lemos.api.model.{UserCreatedResponse, UserRequest, UserResponse, RequestMetadata}
import br.com.lemos.model.AsyncResult.AsyncResult
import br.com.lemos.model._
import br.com.lemos.repository.UserRepository

import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

case object UserDeleted

class UserController(userRepository: UserRepository)(implicit ec: ExecutionContext) extends SLF4JLogging {

  def create(userRequest: UserRequest, requestMetadata: RequestMetadata): AsyncResult[UserCreatedResponse] =
    AsyncResult {
      val user = userRequestToUser(userRequest, requestMetadata.userAgent, requestMetadata.clientIP)

      userRepository
        .create(user)
        .map(id ⇒ Right(UserCreatedResponse(id)))
        .recover {
          case NonFatal(e) ⇒
            log.error("Error creating user: '{}'", e.getMessage)
            Left(InternalServiceError("Internal error when creating user."))
        }
    }

  def findAll: AsyncResult[List[UserResponse]] =
    AsyncResult {
      userRepository.find
        .map(toListOfUserResponse)
        .map(Right(_))
        .recover {
          case NonFatal(e) ⇒
            log.error("Error finding users: '{}'", e.getMessage)
            Left(InternalServiceError("Internal error when finding users."))
        }
    }

  def getById(id: String): AsyncResult[UserResponse] =
    AsyncResult {
      userRepository
        .get(id)
        .map {
          case Some(user) ⇒
            Right(userToUserResponse(user))
          case None ⇒
            Left(NotFoundError(s"User with id '$id' not found."))
        }
        .recover {
          case NonFatal(e) ⇒
            log.error("Error getting user by id '{}'.", e.getMessage)
            Left(InternalServiceError("Internal error when getting user by id."))
        }
    }

  def delete(id: String): AsyncResult[UserDeleted.type] =
    AsyncResult {
      userRepository
        .delete(id)
        .map(_ ⇒ Right(UserDeleted))
        .recover {
          case NonFatal(e) ⇒
            log.error("Error deleting user '{}'.", e.getMessage)
            Left(InternalServiceError("Internal error when deleting user."))
        }
    }

  private val toListOfUserResponse: List[User] ⇒ List[UserResponse] =
    listOfUsers ⇒ listOfUsers.map(userToUserResponse)

  private def userRequestToUser(userRequest: UserRequest, userAgent: Option[String], ip: String): User =
    User(
      id = None,
      username = userRequest.username,
      email = userRequest.email,
      password = userRequest.password,
      metadata = Metadata(userAgent, ip))

  private def userToUserResponse(user: User): UserResponse =
    UserResponse(
      id = user.id.getOrElse("unknown"),
      username = user.username,
      email = user.email,
      password = user.password
    )

}
