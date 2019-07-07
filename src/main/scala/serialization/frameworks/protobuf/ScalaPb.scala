package serialization.frameworks.protobuf

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import com.typesafe.config.ConfigFactory
import serialization.frameworks.protobuf.datamodel.{Person, Sex, ShirtPb, ShortsPb}

object ScalaPb {

}

class SimpleActor1 extends Actor with ActorLogging {
  override def receive: Receive = {
    case msg : Person =>
      log.info(s"received message $msg")

      msg.clothes match {
        case shoe : Person.Clothes.Shoes  => println(s"I got a shoe $shoe")
        case short : Person.Clothes.Shorts => println(s"I got a short $short")
        case shirt : Person.Clothes.Shirt => println(s"I got s short $shirt")
      }


  }
}

object Local_JVM1 extends App {
  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2551
    """.stripMargin
  ).withFallback(ConfigFactory.load("scalapb"))

  val system = ActorSystem("LocalSystem",config)
  val actorSelection = system.actorSelection("akka://RemoteSystem@localhost:2552/user/simpleActor")

  val shirt =  Person.Clothes.Shirt(ShirtPb("Gucci",34))
  val onlineStoreUser = Person(23,List("samuel","ori","chidi"),true,Sex.BOY,shirt)//.toByteArray

  actorSelection ! onlineStoreUser
}

object Remote_JVM1 extends App {
  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2552
    """.stripMargin
  ).withFallback(ConfigFactory.load("scalapb"))

  val system = ActorSystem("RemoteSystem",config)
  val actor = system.actorOf(Props[SimpleActor1],"simpleActor")


}