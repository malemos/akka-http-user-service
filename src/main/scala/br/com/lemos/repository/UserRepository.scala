package br.com.lemos.repository

import br.com.lemos.KamonSupport
import br.com.lemos.model.User
import reactivemongo.api.Cursor
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONObjectID}

import scala.concurrent.{ExecutionContext, Future}

object RepositorySuccess

class UserRepository(mongoCollection: Future[BSONCollection])(implicit ec: ExecutionContext)
    extends BsonSupport
    with KamonSupport {

  private val IdAttribute = "_id"
  private val FindLimit = 1000

  def create(user: User): Future[String] = traceFuture("repository-insert-user") {
    val objectId = BSONObjectID.generate
    val userWithId = user.copy(id = Some(objectId.stringify))

    for {
      mc ← mongoCollection
      result ← mc.insert(true).one[User](userWithId)
    } yield objectId.stringify
  }

  def find: Future[List[User]] = traceFuture("repository-get-users") {
    val query = BSONDocument.empty

    for {
      mc ← mongoCollection
      result ← find(mc, query)
    } yield result
  }

  def get(id: String): Future[Option[User]] = traceFuture("repository-get-user-by-id") {
    val query = BSONDocument(IdAttribute → id)

    for {
      mc ← mongoCollection
      result ← mc.find(query).one[User]
    } yield result.map(_.copy(id = Some(id)))
  }

  def delete(id: String): Future[RepositorySuccess.type] = traceFuture("repository-delete-user") {
    val query = BSONDocument(IdAttribute → id)

    for {
      mc ← mongoCollection
      _ ← mc.remove(query)
    } yield RepositorySuccess
  }

  private def find(collection: BSONCollection, query: BSONDocument) =
    collection
      .find(query)
      .cursor[User]()
      .collect[List](FindLimit, Cursor.DoneOnError[List[User]]())

}
