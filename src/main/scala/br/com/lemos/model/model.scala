package br.com.lemos.model

import reactivemongo.bson.Macros.Annotations.Key

final case class User(@Key("_id") id: Option[String], username: String, email: String, password: String, metadata: Metadata)

final case class Metadata(userAgent: Option[String], clientIp: String)
