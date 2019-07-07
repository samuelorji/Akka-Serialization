package serialization.frameworks.protobuf

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import com.typesafe.config.ConfigFactory
import serialization.cutom.Person
import serialization.frameworks.kryo.House
import serialization.frameworks.protobuf.Datamodel.OnlineStoreUser

object ProtobufSerializer {

}


class SimpleActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case msg =>
      log.info(s"received message $msg")


  }
}

object Local_JVM extends App {
  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2551
    """.stripMargin
  ).withFallback(ConfigFactory.load("protobufserializer"))

  val system = ActorSystem("LocalSystem",config)
  val actorSelection = system.actorSelection("akka://RemoteSystem@localhost:2552/user/simpleActor")

  val onlineStoreUser = OnlineStoreUser.newBuilder()
    .setId(23)
      .setUserName("Samuel")
      .setUserEmail("awesomeorji@gmail.com")
      .setUserName("samuel")
      .build()


  actorSelection ! onlineStoreUser
}

object Remote_JVM extends App {
  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2552
    """.stripMargin
  ).withFallback(ConfigFactory.load("protobufserializer"))

  val system = ActorSystem("RemoteSystem",config)
  val actor = system.actorOf(Props[SimpleActor],"simpleActor")


}