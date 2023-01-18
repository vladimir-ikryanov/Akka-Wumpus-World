import akka.actor.{Actor, ActorRef, PoisonPill}

case class WumpusWorldActor() extends Actor {

  var sendWinMessage = false
  var sendTerminateMessage = false

  private var world = new WumpusWorld(
    worldXDimension = 4, worldYDimension = 4, config = Constants.INITIAL_WUMPUS_CAVE)
  private var agentLocation = new AgentLocation(
    x = 1, y = 1, gazeDirection = AgentGazeDirection.NORTH)

  private var hasArrow = true
  private var isWumpusAlive = true
  private var isGoldGrabbed = false

  def receive: Receive = {
    case Constants.START =>
      println(s"${self.path} agent is ready.")
      println("Current world state:");
      println("\n" + world.toString);

    case Constants.TAKE_DOWN =>
      println("${self.path} agent is terminating.")
      self ! PoisonPill

    case ActorTaken(act) => act ! "begin"

    case Message(act, typeMes, mes) => typeMes match {
      case Constants.GAME_INFORMATION =>
        act ! Message(self, Constants.GAME_INFORMATION, getRoomState.toString)

      case Constants.PROPOSE_TYPE => mes match {
        case Constants.SPELEOLOGIST_TURN_LEFT =>
          turnLeft()
          act ! Message(self, Constants.RESULT_TYPE, Constants.OK_MESSAGE)
          show()

        case Constants.SPELEOLOGIST_TURN_RIGHT =>
          turnRight()
          act ! Message(self, Constants.RESULT_TYPE, Constants.OK_MESSAGE)
          show()

        case Constants.SPELEOLOGIST_MOVE_FORWARD =>
          sendTerminateMessage = moveForward()
          if (sendTerminateMessage)
            act ! Message(self, Constants.RESULT_TYPE, Constants.FAIL_MESSAGE)
          else
            act ! Message(self, Constants.RESULT_TYPE, Constants.OK_MESSAGE)
          show()

        case Constants.SPELEOLOGIST_GRAB =>
          sendWinMessage = grab()
          if (sendWinMessage)
            act ! Message(self, Constants.RESULT_TYPE, Constants.WIN_MESSAGE)
          else
            act ! Message(self, Constants.RESULT_TYPE, Constants.OK_MESSAGE)

        case Constants.SPELEOLOGIST_SHOOT =>
          shoot()
          act ! Message(self, Constants.RESULT_TYPE, Constants.OK_MESSAGE)

        case Constants.SPELEOLOGIST_CLIMB =>
          sendWinMessage = climb()
          if (sendWinMessage)
            act ! Message(self, Constants.RESULT_TYPE, Constants.WIN_MESSAGE)
          else
            Message(self, Constants.RESULT_TYPE, Constants.FAIL_MESSAGE)

        case _ => println(Constants.WRONG_COMMAND_MSG)
      }
      case _ => println(Constants.WRONG_COMMAND_MSG)
    }
    case _ => println(Constants.WRONG_COMMAND_MSG)
  }

  private def show(): Unit = println("The world state after the action:" + "\n" + world)

  private def turnLeft(): Unit = {
    agentLocation = world.goLeft(agentLocation)
  }

  private def turnRight(): Unit = {
    agentLocation = world.goRight(agentLocation)
  }

  private def moveForward() = {
    agentLocation = world.moveForward(agentLocation)
    (isWumpusAlive && world.getRoomWithWumpus == agentLocation.getRoom) || world.isPit(agentLocation.getRoom)
  }

  private def grab(): Boolean = {
    if (world.getRoomWithGold == agentLocation.getRoom) isGoldGrabbed = true
    isGoldGrabbed
  }

  private def shoot(): Unit = {
    if (hasArrow && isAgentFacingWumpus(agentLocation)) isWumpusAlive = false
  }

  private def climb() = agentLocation.getRoom == new Room(1, 1) && isGoldGrabbed

  def getRoomState: RoomState = {
    val result = new RoomState
    val location = agentLocation
    val roomsAround = Array(
      new Room(location.getX - 1, location.getY),
      new Room(location.getX + 1, location.getY),
      new Room(location.getX, location.getY - 1),
      new Room(location.getX, location.getY + 1))
    var validRooms = Array.empty[Room]
    for (r <- roomsAround) {
      if (r.getX <= world.worldXDimension && r.getX > 0 && r.getY <= world.worldYDimension && r.getY > 0)
        validRooms +:= r
    }
    for (r <- validRooms) {
      if (r == world.getRoomWithWumpus)
        result.setStench()
      if (world.isPit(r))
        result.setBreeze()
    }
    if (location.getRoom.equals(world.getRoomWithGold))
      result.setGlitter()
    if (!isWumpusAlive)
      result.setScream()
    result
  }

  private def isAgentFacingWumpus(pos: AgentLocation): Boolean = {
    val wumpusRoom = world.getRoomWithWumpus
    pos.getGazeDirection match {
      case AgentGazeDirection.NORTH =>
        return pos.getX == wumpusRoom.getX && pos.getY < wumpusRoom.getY
      case AgentGazeDirection.SOUTH =>
        return pos.getX == wumpusRoom.getX && pos.getY > wumpusRoom.getY
      case AgentGazeDirection.EAST =>
        return pos.getY == wumpusRoom.getY && pos.getX < wumpusRoom.getX
      case AgentGazeDirection.WEST =>
        return pos.getY == wumpusRoom.getY && pos.getX > wumpusRoom.getX
    }
    false
  }
}

object Constants {
  val START = "start"
  val TAKE_DOWN = "takeDown"
  val WRONG_COMMAND_MSG = "Wrong command!"
  val WUMPUS_SERVICE_DESCRIPTION = "wumpus_world"
  val NAVIGATOR_SERVICE_DESCRIPTION = "navigator"
  val GO_INSIDE = "go_inside"
  val WUMPUS_WORLD_DIGGER_CONVERSATION_ID = "digger_world"
  val NAVIGATOR_DIGGER_CONVERSATION_ID = "digger_navigator"
  val NAVIGATOR_AGENT_TYPE = "navigator_agent"
  val INITIAL_WUMPUS_CAVE = "* * * P W G * * * * * * S * P * "
  val OK_MESSAGE = "OK"
  val FAIL_MESSAGE = "FAIL"
  val WIN_MESSAGE = "WIN"
  val SPELEOLOGIST_TURN_LEFT = "SPELEOLOGIST_LOOK_LEFT"
  val SPELEOLOGIST_TURN_RIGHT = "SPELEOLOGIST_TURN_RIGHT"
  val SPELEOLOGIST_MOVE_FORWARD = "SPELEOLOGIST_MOVE_FORWARD"
  val SPELEOLOGIST_GRAB = "SPELEOLOGIST_GRAB"
  val SPELEOLOGIST_SHOOT = "SPELEOLOGIST_SHOOT"
  val SPELEOLOGIST_CLIMB = "SPELEOLOGIST_CLIMB"
  val GAME_INFORMATION = "INFORMATION"
  val GAME_COMMAND = "GAME_COMMAND"
  val ADVICE_TYPE = "ADVICE_TYPE"
  val PROPOSE_TYPE = "PROPOSE_TYPE"
  val RESULT_TYPE = "RESULT_TYPE"
  val ADVICE_PROPOSAL = "Any advice, navigator?"
  val INFORMATION_PROPOSAL_NAVIGATOR = "Please tell me what's around me."
  val INFORMATION_PROPOSAL_SPELEOLOGIST = "Here's the information: "

  val ACTION_PROPOSAL1 = "You should "
  val ACTION_PROPOSAL2 = "I think you should "
  val ACTION_PROPOSAL3 = "You might want to "

  val MESSAGE_LEFT = "turn left."
  val MESSAGE_RIGHT = "turn right."
  val MESSAGE_FORWARD = "move forward."
  val MESSAGE_GRAB = "grab the gold."
  val MESSAGE_SHOOT = "shoot."
  val MESSAGE_CLIMB = "climb the ladder."
}

case class Message(from: ActorRef, typeMes: String, mes: String)

case class ActorTaken(from: ActorRef)