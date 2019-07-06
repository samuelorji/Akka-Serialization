package serialization.frameworks.kryo

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import com.typesafe.config.ConfigFactory
import serialization.cutom.{Human, Person}


case class House (address : String, number : Int)
object KryoSerializer {

}

class SimpleActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case person : Person =>
      log.info(s"received message $person")

    case house : House =>
      log.info(s"received message $house")
  }

}

object Local_JVM extends App {
  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2551
    """.stripMargin
  ).withFallback(ConfigFactory.load("kryoserializer"))

  val system = ActorSystem("LocalSystem",config)
  val actorSelection = system.actorSelection("akka://RemoteSystem@localhost:2552/user/simpleActor")


  actorSelection ! House("trolley street",109)
}

object Remote_JVM extends App {
  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2552
    """.stripMargin
  ).withFallback(ConfigFactory.load("kryoserializer"))

  val system = ActorSystem("RemoteSystem",config)
  val actor = system.actorOf(Props[SimpleActor],"simpleActor")


}

