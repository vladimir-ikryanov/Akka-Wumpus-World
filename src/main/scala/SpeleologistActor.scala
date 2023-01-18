import akka.actor.{Actor, ActorRef, PoisonPill}

class SpeleologistActor(world: ActorRef, navigator: ActorRef) extends Actor {

  def receive: Receive = {
    case Constants.START =>
      println(s"${self.path} agent is ready.")

    case "begin" => self ! Message(self, Constants.ADVICE_PROPOSAL, "")

    case Constants.TAKE_DOWN =>
      println("${self.path} agent is terminating.")
      self ! PoisonPill

    case Message(act, typeMes, mes) => typeMes match {
      case Constants.ADVICE_PROPOSAL =>
        println(Constants.ADVICE_PROPOSAL)
        navigator ! Message(self, Constants.ADVICE_PROPOSAL, "")

      case Constants.INFORMATION_PROPOSAL_NAVIGATOR =>
        world ! Message(self, Constants.GAME_INFORMATION, "")

      case Constants.GAME_INFORMATION =>
        println(Constants.INFORMATION_PROPOSAL_SPELEOLOGIST + mes)
        navigator ! Message(self, Constants.INFORMATION_PROPOSAL_SPELEOLOGIST, mes)

      case Constants.ADVICE_TYPE =>
        val action = getActionFromMessage(mes)
        var content = ""
        action match {
          case Commands.GO_FORWARD => content = Constants.SPELEOLOGIST_MOVE_FORWARD
          case Commands.GO_LEFT => content = Constants.SPELEOLOGIST_TURN_LEFT
          case Commands.GO_RIGHT => content = Constants.SPELEOLOGIST_TURN_RIGHT
          case Commands.GRAB => content = Constants.SPELEOLOGIST_GRAB
          case Commands.SHOOT => content = Constants.SPELEOLOGIST_SHOOT
          case Commands.CLIMB => content = Constants.SPELEOLOGIST_CLIMB
          case _ =>
        }
        world ! Message(self, Constants.PROPOSE_TYPE, content)

      case Constants.RESULT_TYPE => mes match {
        case Constants.OK_MESSAGE =>
          self ! Message(self, Constants.ADVICE_PROPOSAL, "")

        case Constants.FAIL_MESSAGE =>
          println("Speleologist: You are dead!")
          self ! PoisonPill

        case Constants.WIN_MESSAGE =>
          println("Speleologist: The speleologist survived and won!")
          self ! PoisonPill

        case _ => println(Constants.WRONG_COMMAND_MSG)
      }
      case _ => println(Constants.WRONG_COMMAND_MSG)
    }
    case _ => println(Constants.WRONG_COMMAND_MSG)
  }

  private def getActionFromMessage(instruction: String): String = {
    var result: String = ""
    for (command <- Commands.LIST) {
      val pattern = ("\\b" + command + "\\b").r
      val res = pattern findFirstIn instruction
      result = res.getOrElse("")
      if (result.nonEmpty)
        return result
    }
    result
  }

  object Commands {
    val GO_LEFT = "left"
    val GO_RIGHT = "right"
    val GO_FORWARD = "forward"
    val GRAB = "grab"
    val SHOOT = "shoot"
    val CLIMB = "climb"
    val LIST: Array[String] = Array(GO_LEFT, GO_RIGHT, GO_FORWARD, GRAB, SHOOT, CLIMB)
  }
}
