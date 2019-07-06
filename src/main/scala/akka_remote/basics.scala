package akka_remote

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import com.typesafe.config.ConfigFactory

object basics {

}

class BasicActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case msg : String =>
      log.info(s"received message $msg from ${sender()}")

  }
}

object Recap_Local extends App {

  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2551
    """.stripMargin
  ).withFallback(ConfigFactory.load())

  val system = ActorSystem("Local", config)
  val actorSelection = system.actorSelection("akka://Remote@localhost:2552/user/remoteActor")

  actorSelection ! "Hello from Local"
}


object Recap_Remote extends App {
  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2552
    """.stripMargin
  ).withFallback(ConfigFactory.load())

  val system = ActorSystem("Remote", config)
  val actor  = system.actorOf(Props[BasicActor], "remoteActor")

}

