package br.com.lemos.repository

import java.util.concurrent.TimeUnit

import org.scalatest._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import br.com.lemos.config.TestesConfig

trait BaseRepositoryTest
    extends AsyncWordSpec
    with RepositoryModuleTest
    with TestesConfig
    with MustMatchers
    with OptionValues
    with BsonSupport
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  private val defaultDuration = Duration(10, TimeUnit.SECONDS)

  override protected def beforeEach(): Unit = {
    Await.ready(collection.flatMap(_.drop(failIfNotFound = false)), defaultDuration)
    ()
  }

  override protected def afterAll(): Unit = {
    Await.ready(collection.flatMap(_.drop(failIfNotFound = false)), defaultDuration)
    Await.ready(connection.map(_.askClose()(defaultDuration)), defaultDuration)
    ()
  }
}
