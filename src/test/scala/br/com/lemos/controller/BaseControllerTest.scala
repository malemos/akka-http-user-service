package br.com.lemos.controller

import br.com.lemos.repository.UserRepository
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.{AsyncWordSpec, EitherValues, MustMatchers}

import scala.concurrent.ExecutionContext

trait BaseControllerTest
    extends AsyncWordSpec
    with AsyncMockFactory
    with MustMatchers
    with EitherValues
    with UserControllerModule {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
  override val userRepository: UserRepository = mock[UserRepository]

}
