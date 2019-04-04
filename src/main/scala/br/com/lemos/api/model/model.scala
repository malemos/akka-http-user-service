package br.com.lemos.api.model

final case class UserRequest(username: String, email: String, password: String)

final case class UserResponse(id: String, username: String, email: String, password: String)

final case class UserCreatedResponse(id: String)

final case class RequestMetadata(userAgent: Option[String], clientIP: String)
