package generic

import cats.Monoid

object MonoidExtra {
  def whenMonoid[A: Monoid](cond: Boolean)(a: => A): A = {
    if cond then a else Monoid.empty[A]
  }
}
