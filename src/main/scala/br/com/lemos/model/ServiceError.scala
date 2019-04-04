package br.com.lemos.model

sealed abstract class ServiceError(val message: String)

final case class InternalServiceError(override val message: String) extends ServiceError(message)

final case class NotFoundError(override val message: String) extends ServiceError(message)
