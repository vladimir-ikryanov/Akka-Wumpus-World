class AgentLocation {
  var room: Room = new Room()
  var gazeDirection: String = AgentGazeDirection.NORTH

  def this(x: Int, y: Int, gazeDirection: String) {
    this()
    room = new Room(x, y)
    this.gazeDirection = gazeDirection
  }

  def getRoom: Room = room

  def getX: Int = room.getX

  def getY: Int = room.getY

  def getGazeDirection: String = gazeDirection

  override def toString: String = room.toString + "->" + gazeDirection

  def canEqual(other: Any): Boolean = other.isInstanceOf[AgentLocation]

  override def equals(other: Any): Boolean = other match {
    case that: AgentLocation =>
      (that canEqual this) &&
        room == that.room &&
        gazeDirection == that.gazeDirection
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(room, gazeDirection)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object AgentGazeDirection {
  val NORTH = "FacingNorth"
  val SOUTH = "FacingSouth"
  val EAST = "FacingEast"
  val WEST = "FacingWest"
}
