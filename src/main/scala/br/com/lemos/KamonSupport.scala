package br.com.lemos

import kamon.Kamon

import scala.concurrent.{ExecutionContext, Future}

trait KamonSupport {

  def traceFuture[T](name: String)(f: => Future[T])(implicit ec: ExecutionContext): Future[T] = {
    val span = Kamon.buildSpan(name).start()
    f.transform(
      result => {
        span.finish()
        result
      },
      throwable => {
        span.finish()
        throwable
      }
    )
  }

}
