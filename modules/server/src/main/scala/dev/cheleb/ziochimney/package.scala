package dev.cheleb.ziochimney

import zio.*
import io.scalaland.chimney.Transformer

extension [A](zio: ZIO[Any, Throwable, A]) {
  def mapInto[B](using transformer: Transformer[A, B]): ZIO[Any, Throwable, B] =
    zio.map(a => transformer.transform(a))
}
