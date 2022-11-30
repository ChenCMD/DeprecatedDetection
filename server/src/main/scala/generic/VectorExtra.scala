package generic

object VectorExtra {
  def when[A](cond: Boolean)(vec: => Vector[A]): Vector[A] = {
    if cond then vec else Vector.empty
  }
}
