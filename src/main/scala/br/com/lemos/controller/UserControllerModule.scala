package br.com.lemos.controller

import br.com.lemos.repository.UserRepository

import scala.concurrent.ExecutionContext

trait UserControllerModule {

  implicit val executionContext: ExecutionContext
  lazy val userController: UserController = new UserController(userRepository)
  val userRepository: UserRepository

}
