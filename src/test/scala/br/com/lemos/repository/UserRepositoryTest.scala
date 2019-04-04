package br.com.lemos.repository

import br.com.lemos.model.{User, Metadata}
import reactivemongo.bson.BSONDocument
import br.com.lemos.tags.RequiresDb

@RequiresDb
class UserRepositoryTest extends BaseRepositoryTest {

  override val dbName = "test_db"
  override val collectionName = "users"

  "UserRepository" when {
    "creating" should {
      "create an user" in {
        // given
        val user = User(None, "username", "teste@teste.com", "password", Metadata(Some("chrome"), "127.0.0.1"))

        // when
        for {
          id ← userRepository.create(user)

          query = BSONDocument("_id" → id, "username" → "username", "email" → "teste@teste.com", "password" → "password")
          result ← collection.flatMap(_.find(query).one[User])
        } yield {

          // then
          result mustBe Some(user.copy(id = Some(id)))
        }
      }
    }

    "finding" should {
      "find users" in {
        // given
        val a1 = User(None, "username1", "teste1@teste.com", "password", Metadata(None, ""))
        val a2 = User(None, "username2", "teste2@teste.com", "password", Metadata(None, ""))
        val a3 = User(None, "username3", "teste3@teste.com", "password", Metadata(None, ""))

        // when
        for {
          id1 ← userRepository.create(a1)
          id2 ← userRepository.create(a2)
          id3 ← userRepository.create(a3)
          result ← userRepository.find
        } yield {

          // then
          result.map(_.id.get) must contain only (id1, id2, id3)
        }
      }

      "return empty list if no users" in {
        // when
        userRepository.find.map { result ⇒
          // then
          result mustBe empty
        }
      }
    }

    "getting by id" should {
      "get user by id" in {
        // given
        val user = User(None, "username", "teste@teste.com", "password", Metadata(None, ""))

        // when
        for {
          id ← userRepository.create(user)
          result ← userRepository.get(id)
        } yield {

          // then
          result mustBe Some(user.copy(id = Some(id)))
        }
      }

      "return None if user not found" in {
        // given
        val nonExistingId = "5a77488f0100000100a45ca9"

        // when
        userRepository.get(nonExistingId).map { result ⇒
          // then
          result mustBe None
        }
      }
    }

    "deleting" should {
      "delete by id" in {
        // given
        val user = User(None, "username", "teste@teste.com", "password", Metadata(None, ""))

        // when
        for {
          id ← userRepository.create(user)
          _ ← userRepository.delete(id)
          getByIdResult ← userRepository.get(id)
        } yield {

          // then
          getByIdResult mustBe None
        }
      }
    }
  }
}
