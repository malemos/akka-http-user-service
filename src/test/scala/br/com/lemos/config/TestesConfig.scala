package br.com.lemos.config

import com.typesafe.config.ConfigFactory
import com.typesafe.config.Config

trait TestesConfig {

  val config: Config = ConfigFactory.load()

  val serverPort: Int = config.getInt("server.port")
  val serverInterface: String = config.getString("server.interface")

  val mongoUri: String = config.getString("mongo.uri")
  val dbName: String = config.getString("mongo.dbName")
  val collectionName: String = config.getString("mongo.collectionName")

}
