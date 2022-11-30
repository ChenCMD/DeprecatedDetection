package generic

import cats.effect.IO
import cats.Applicative

object IOExtra {
  def whenA[A](cond: Boolean)(action: => IO[A]): IO[Option[A]] = {
    if cond then action.map(Some.apply) else IO.none
  }

  def unlessA[A](cond: Boolean)(action: => IO[A]): IO[Option[A]] = {
    if cond then IO.none else action.map(Some.apply)
  }
}
