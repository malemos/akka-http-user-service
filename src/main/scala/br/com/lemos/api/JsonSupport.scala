package br.com.lemos.api

import akka.http.scaladsl.marshalling.{Marshaller, ToResponseMarshaller}
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.model._
import br.com.lemos.api.model.{UserCreatedResponse, UserRequest, UserResponse, ErrorResponse}
import br.com.lemos.controller.UserDeleted
import br.com.lemos.model.{InternalServiceError, NotFoundError, ServiceError}
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport
import io.circe.generic.semiauto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Printer}

trait JsonSupport extends ErrorAccumulatingCirceSupport {

  implicit val printer: Printer = Printer.noSpaces

  implicit val userRequestDecoder: Decoder[UserRequest] = deriveDecoder
  implicit val userResponseEncoder: Encoder[UserResponse] = deriveEncoder

  implicit val userCreatedResponseEncoder: Encoder[UserCreatedResponse] = deriveEncoder

  implicit val errorResponseEncoder: Encoder[ErrorResponse] = deriveEncoder

  implicit val userCreatedResponseMarshaller: ToResponseMarshaller[UserCreatedResponse] = {
    Marshaller.withFixedContentType(MediaTypes.`application/json`) { acr =>
      HttpResponse(
        status = StatusCodes.Created,
        entity = HttpEntity(`application/json`, printer.pretty(acr.asJson))
      )
    }
  }

  implicit val userDeletedMarshaller: ToResponseMarshaller[UserDeleted.type] =
    Marshaller.withFixedContentType(MediaTypes.`application/json`) { _ =>
      HttpResponse(
        status = StatusCodes.NoContent,
        entity = HttpEntity.empty(ContentTypes.`application/json`)
      )
    }

  implicit val errorMarshaller: ToResponseMarshaller[ServiceError] = Marshaller.combined {
    case InternalServiceError(msg) ⇒
      (StatusCodes.InternalServerError, ErrorResponse(msg))
    case NotFoundError(msg) ⇒
      (StatusCodes.NotFound, ErrorResponse(msg))
  }

}
