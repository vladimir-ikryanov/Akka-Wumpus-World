import scala.collection.mutable

class WumpusWorld(var worldXDimension: Int = 0, var worldYDimension: Int = 0) {
  private var start = new AgentLocation(1, 1, AgentGazeDirection.NORTH)
  private var wumpus = new Room()
  private var gold = new Room()
  private val pits = new mutable.LinkedHashSet[Room]
  private var allRooms = new mutable.HashSet[Room]

  def this(worldXDimension: Int, worldYDimension: Int, config: String) {
    this()
    this.worldXDimension = worldXDimension
    this.worldYDimension = worldYDimension
    if (config.length != 2 * worldXDimension * worldYDimension)
      throw new IllegalStateException("Error: Wrong Wumpus world dimension. Must be 4x4.")
    var i = 0
    while ( {
      i < config.length
    }) {
      val c = config.charAt(i)
      val room = new Room(i / 2 % worldXDimension + 1, worldYDimension - i / 2 / worldXDimension)
      c match {
        case 'S' =>
          start = new AgentLocation(room.getX, room.getY, AgentGazeDirection.NORTH)
        case 'W' =>
          setRoomWithWumpus(room)
        case 'G' =>
          setRoomWithGold(room)
        case 'P' =>
          setRoomWithPit(room)
        case _ =>
      }

      {
        i += 1
      }
    }
    allRooms = getAllRooms
  }

  def setRoomWithPit(room: Room): Unit = {
    pits.add(room)
  }

  def setRoomWithWumpus(room: Room): Unit = {
    wumpus = room
  }

  def setRoomWithGold(room: Room): Unit = {
    gold = room
  }

  def getRoomWithWumpus: Room = wumpus

  def getRoomWithGold: Room = gold

  def isPit(room: Room): Boolean = pits.contains(room)

  def moveForward(position: AgentLocation): AgentLocation = {
    var x = position.getX
    var y = position.getY
    position.getGazeDirection match {
      case AgentGazeDirection.NORTH => y += 1
      case AgentGazeDirection.SOUTH => y -= 1
      case AgentGazeDirection.EAST => x += 1
      case AgentGazeDirection.WEST => x -= 1
    }
    val room = new Room(x, y)
    start = if (allRooms.contains(room))
      new AgentLocation(x, y, position.getGazeDirection)
    else position
    start
  }

  def goLeft(position: AgentLocation): AgentLocation = {
    var orientation = ""
    position.getGazeDirection match {
      case AgentGazeDirection.NORTH =>
        orientation = AgentGazeDirection.WEST
      case AgentGazeDirection.SOUTH =>
        orientation = AgentGazeDirection.EAST
      case AgentGazeDirection.EAST =>
        orientation = AgentGazeDirection.NORTH
      case AgentGazeDirection.WEST =>
        orientation = AgentGazeDirection.SOUTH
    }
    start = new AgentLocation(position.getX, position.getY, orientation)
    start
  }

  def goRight(position: AgentLocation): AgentLocation = {
    var orientation = ""
    position.getGazeDirection match {
      case AgentGazeDirection.NORTH =>
        orientation = AgentGazeDirection.EAST
      case AgentGazeDirection.SOUTH =>
        orientation = AgentGazeDirection.WEST
      case AgentGazeDirection.EAST =>
        orientation = AgentGazeDirection.SOUTH
      case AgentGazeDirection.WEST =>
        orientation = AgentGazeDirection.NORTH
    }
    start = new AgentLocation(position.getX, position.getY, orientation)
    start
  }

  def getAllRooms: mutable.HashSet[Room] = {
    val result = new mutable.HashSet[Room]
    for (x <- 1 to worldXDimension; y <- 1 to worldYDimension) {
      result += new Room(x, y)
    }
    result
  }

  override def toString: String = {
    val builder = new mutable.StringBuilder
    var y = worldYDimension
    while ( {
      y >= 1
    }) {
      var x = 1
      while ( {
        x <= worldXDimension
      }) {
        val room = new Room(x, y)
        var txt = ""
        if (room == start.getRoom) txt += "S"
        if (room == gold) txt += "G"
        if (room == wumpus) txt += "W"
        if (isPit(room)) txt += "P"
        if (txt.isEmpty) txt = "* "
        else if (txt.length == 1) txt += " "
        else if (txt.length > 2) {
          txt = txt.substring(0, 2)
        }
        builder.append(txt)

        {
          x += 1;
        }
      }
      builder.append("\n")

      {
        y -= 1;
      }
    }
    builder.toString
  }
}
