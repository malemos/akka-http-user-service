package br.com.lemos.repository

import br.com.lemos.model.{User, Metadata}
import reactivemongo.bson.{BSONDocumentHandler, Macros}

trait BsonSupport {
  implicit val userHandler: BSONDocumentHandler[User] = Macros.handler[User]
  implicit val userMetadataHandler: BSONDocumentHandler[Metadata] = Macros.handler[Metadata]
}
