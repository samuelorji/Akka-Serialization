package serialization.cutom

import akka.actor.{Actor, ActorLogging, ActorSelection, ActorSystem, Props}
import akka.serialization.Serializer

import com.typesafe.config.ConfigFactory


case class Person(name : String , age : Int)

class PersonSerializer extends Serializer //needed for serialization
{
  private final val id : Int = 9001
  private final val SEPARATOR = "//"
  override def identifier: Int = id //just a number used to identify this serializer

  override def toBinary(o: AnyRef): Array[Byte] = {
    o match {
      case person@Person(name,age) =>
        println(s"serializing $person")
        val personString = s"[$name$SEPARATOR$age]"
        personString.getBytes()

      case _ => throw new IllegalArgumentException("Expected type Person")
    }
  }

  override def includeManifest: Boolean = false //we don't need to include the manifest because, this serializer only supports Person case class

  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {

    val string = new String(bytes)
    val personSubstring = string.substring(1, string.length -1)
    val personargs = personSubstring.split(SEPARATOR)
    val person =  Person(personargs(0),personargs(1).toInt)
    println(s"deserialized to $person")
    person
  }
}
class CustomSerialization {

}

class SimpleActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case person : Person =>
      log.info(s"received message $person")
  }

}

object Local_JVM extends App {
  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2551
    """.stripMargin
  ).withFallback(ConfigFactory.load("customserialization"))

  val system = ActorSystem("LocalSystem",config)
  val actorSelection = system.actorSelection("akka://RemoteSystem@localhost:2552/user/simpleActor")

  actorSelection! Person("Jennifer",90)
}

object Remote_JVM extends App {
  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2552
    """.stripMargin
  ).withFallback(ConfigFactory.load("customserialization"))

  val system = ActorSystem("RemoteSystem",config)
  val actor = system.actorOf(Props[SimpleActor],"simpleActor")


}


