class Room(val x: Int = 1, val y: Int = 1) {
  def getX: Int = x

  def getY: Int = y

  def canEqual(other: Any): Boolean = other.isInstanceOf[Room]

  override def equals(other: Any): Boolean = other match {
    case that: Room =>
      (that canEqual this) &&
        x == that.x &&
        y == that.y
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(x, y)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString: String = "[" + x + ":" + y + "]"
}
