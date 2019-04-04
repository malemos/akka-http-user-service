package br.com.lemos.controller

import br.com.lemos.api.model.{UserCreatedResponse, UserRequest, UserResponse, RequestMetadata}
import br.com.lemos.model.{User, InternalServiceError, Metadata, NotFoundError}
import br.com.lemos.repository.RepositorySuccess

import scala.concurrent.Future

class UserControllerTest extends BaseControllerTest {

  "UserController" when {
    "creating" should {
      "create an user" in {
        // given
        val user = User(None, "username", "teste@teste.com", "password", Metadata(Some("curl"), "52.22.43.14"))

        val repositoryResult = Future.successful("username")
        (userRepository.create _).expects(user).returning(repositoryResult)

        val userRequest = UserRequest("username", "teste@teste.com", "password")
        val requestMetadata = RequestMetadata(Some("curl"), "52.22.43.14")

        // when
        val result = userController.create(userRequest, requestMetadata).value

        // then
        result.map { r ⇒
          r mustBe Right(UserCreatedResponse("username"))
        }
      }

      "handle repository errors" in {
        // given
        val repositoryResult = Future.failed(new RuntimeException("DB not reachable"))
        (userRepository.create _).expects(*).returning(repositoryResult)

        val userRequest = UserRequest("username", "teste@teste.com", "password")
        val requestMetadata = RequestMetadata(None, "")

        // when
        val result = userController.create(userRequest, requestMetadata).value

        // then
        result.map { r ⇒
          r.left.value mustBe a[InternalServiceError]
          r.left.value.message must not be empty
        }
      }
    }

    "finding all" should {
      "find all users" in {
        // given
        val user = User(Some("1"), "username", "teste@teste.com", "password", Metadata(Some("curl"), ""))

        val repositoryResult = Future.successful(List(user))
        (userRepository.find _).expects().returning(repositoryResult)

        // when
        val result = userController.findAll.value

        // then
        result.map { r ⇒
          r mustBe Right(List(UserResponse("1", "username", "teste@teste.com", "password")))
        }
      }

      "handle repository errors" in {
        // given
        val repositoryResult = Future.failed(new RuntimeException("DB not reachable"))
        (userRepository.find _).expects().returning(repositoryResult)

        // when
        val result = userController.findAll.value

        // then
        result.map { r ⇒
          r.left.value mustBe a[InternalServiceError]
          r.left.value.message must not be empty
        }
      }
    }

    "getting by id" should {
      "get user by id" in {
        // given
        val user = User(Some("1"), "username", "teste@teste.com", "password", Metadata(Some("curl"), ""))

        val repositoryResult = Future.successful(Some(user))
        (userRepository.get _).expects("1").returning(repositoryResult)

        // when
        val result = userController.getById("1").value

        // then
        result.map { r ⇒
          r mustBe Right(UserResponse("1", "username", "teste@teste.com", "password"))
        }
      }

      "return not found" in {
        // given
        val repositoryResult = Future.successful(None)
        (userRepository.get _).expects("1").returning(repositoryResult)

        // when
        val result = userController.getById("1").value

        // then
        result.map { r ⇒
          r.left.value mustBe a[NotFoundError]
          r.left.value.message must not be empty
        }
      }

      "handle repository errors" in {
        // given
        val repositoryResult = Future.failed(new RuntimeException("DB not reachable"))
        (userRepository.get _).expects(*).returning(repositoryResult)

        // when
        val result = userController.getById("1").value

        // then
        result.map { r ⇒
          r.left.value mustBe a[InternalServiceError]
          r.left.value.message must not be empty
        }
      }
    }

    "deleting" should {
      "delete an user" in {
        // given
        val repositoryResult = Future.successful(RepositorySuccess)
        (userRepository.delete _).expects("1").returning(repositoryResult)

        // when
        val result = userController.delete("1").value

        // then
        result.map { r ⇒
          r mustBe Right(UserDeleted)
        }
      }

      "handle repository errors" in {
        // given
        val repositoryResult = Future.failed(new RuntimeException("DB not reachable"))
        (userRepository.delete _).expects(*).returning(repositoryResult)

        // when
        val result = userController.delete("1").value

        // then
        result.map { r ⇒
          r.left.value mustBe a[InternalServiceError]
          r.left.value.message must not be empty
        }
      }
    }
  }
}
