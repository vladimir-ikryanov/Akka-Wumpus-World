import akka.actor.{ActorSystem, Props}

object Main extends App {
  val system = ActorSystem("WumpusWorld")
  val wumpusWorld = system.actorOf(Props[WumpusWorldActor], name = "WumpusWorld")
  val navigator = system.actorOf(Props[NavigatorActor], name = "Navigator")

  wumpusWorld ! Constants.START
  navigator ! Constants.START

  val speleologist = system.actorOf(
    Props(classOf[SpeleologistActor], wumpusWorld, navigator), name = "Speleologist")

  speleologist ! Constants.START
  wumpusWorld ! ActorTaken(speleologist)
}
