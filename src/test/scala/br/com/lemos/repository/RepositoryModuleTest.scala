package br.com.lemos.repository

import reactivemongo.api.{MongoConnection, MongoDriver}
import reactivemongo.api.collections.bson.BSONCollection

import scala.concurrent.{ExecutionContext, Future}
import br.com.lemos.config.TestesConfig
import reactivemongo.api.MongoConnectionOptions

trait RepositoryModuleTest {
  this: TestesConfig ⇒

  implicit val executionContext: ExecutionContext

  lazy val userRepository: UserRepository = new UserRepository(collection)

  MongoConnectionOptions
  protected lazy val connection: Future[MongoConnection] =
    Future.fromTry(MongoDriver().connection(mongoUri, None, true))

  protected lazy val collection: Future[BSONCollection] =
    for {
      conn <- connection
      db ← conn.database(dbName)
    } yield db.collection(collectionName)

}
