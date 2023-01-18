import akka.actor.{Actor, PoisonPill}

class NavigatorActor extends Actor {

  def receive: Receive = {
    case Constants.START =>
      println(s"${self.path} agent is ready.")

    case Constants.TAKE_DOWN =>
      println("${self.path} agent is terminating.")
      self ! PoisonPill

    case Message(act, typeMes, mes) => typeMes match {
      case Constants.ADVICE_PROPOSAL =>
        println(Constants.INFORMATION_PROPOSAL_NAVIGATOR)
        act ! Message(self, Constants.INFORMATION_PROPOSAL_NAVIGATOR, "")

      case Constants.INFORMATION_PROPOSAL_SPELEOLOGIST =>
        val advice = getAdvice(mes)
        act ! Message(self, Constants.ADVICE_TYPE, advice)
        println(advice)

      case _ => println(Constants.WRONG_COMMAND_MSG)
    }
    case _ => println(Constants.WRONG_COMMAND_MSG)
  }

  private def getAdvice(message: String) = {
    var stench = false
    var breeze = false
    var glitter = false
    var scream = false
    var proposedAction = ""

    STATES foreach (x => {
      val pattern = ("\\b" + x + "\\b").r
      val res = (pattern findFirstIn message).getOrElse("")
      res match {
        case "Stench" =>
          stench = true
        case "Breeze" =>
          breeze = true
        case "Glitter" =>
          glitter = true
        case "Scream" =>
          scream = true
        case _ =>
      }
    })

    if (glitter) {
      proposedAction = Constants.MESSAGE_GRAB
      time += 1
    } else
      time match {
        case 0 =>
          proposedAction = Constants.MESSAGE_FORWARD
          time += 1
        case 1 =>
          proposedAction = Constants.MESSAGE_RIGHT
          time += 1
        case 2 =>
          proposedAction = Constants.MESSAGE_FORWARD
          time += 1
        case 3 =>
          proposedAction = Constants.MESSAGE_LEFT
          time += 1
        case 4 =>
          proposedAction = Constants.MESSAGE_FORWARD
          time += 1
        case 5 =>
          proposedAction = Constants.MESSAGE_LEFT
          time += 1
        case 6 =>
          proposedAction = Constants.MESSAGE_FORWARD
          time += 1
      }

    val rand = 1 + (Math.random * 3).toInt
    rand match {
      case 1 =>
        Constants.ACTION_PROPOSAL1 + proposedAction
      case 2 =>
        Constants.ACTION_PROPOSAL2 + proposedAction
      case 3 =>
        Constants.ACTION_PROPOSAL3 + proposedAction
      case _ =>
        ""
    }
  }

  var time = 0
  var STATES: Array[String] = Array("Stench", "Breeze", "Glitter", "Scream")
}
