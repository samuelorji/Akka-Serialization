package serialization.frameworks.avro

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.serialization.Serializer

import com.sksamuel.avro4s.{AvroInputStream, AvroOutputStream, AvroSchema}
import com.typesafe.config.ConfigFactory
import serialization.custom.Person
import serialization.frameworks.kryo.House


case class Screen(id : Int,maker : String )
case class Battery(id : Int,maker : String )
case class Phone(id : Int , screen : Screen , battery : Battery)


object Avroserializer extends App {
  println(AvroSchema[Phone])

}

class CustAvroSerializers extends Serializer {

  val phoneSchema = AvroSchema[Phone]
  private final val ID : Int = 9876
  override def identifier: Int = ID

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case phone : Phone =>
      val outputStream = new ByteArrayOutputStream()
      val avroStream = AvroOutputStream.binary[Phone].to(outputStream).build(phoneSchema)
      avroStream.write(phone)
      avroStream.flush()
      avroStream.close()

      outputStream.toByteArray
    case _ => throw new IllegalArgumentException("Expected only Phone type ")

  }

  override def includeManifest: Boolean = true

  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {
    val inputStream = new ByteArrayInputStream(bytes)
    val avroInputStream = AvroInputStream.binary[Phone].from(inputStream).build(phoneSchema)

    val phone = avroInputStream.iterator.next()
    avroInputStream.close() //not to leak resources
    phone

  }
}

class SimpleActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case person : Person =>
      log.info(s"received message $person")

    case phone : Phone =>
      log.info(s"received message $phone")
  }

}

object Local_JVM extends App {
  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2551
    """.stripMargin
  ).withFallback(ConfigFactory.load("avroserializer"))

  val system = ActorSystem("LocalSystem",config)
  val actorSelection = system.actorSelection("akka://RemoteSystem@localhost:2552/user/simpleActor")


  actorSelection ! Phone(10, Screen(100,"samsung"),Battery(1000,"huawei"))
}

object Remote_JVM extends App {
  val config = ConfigFactory.parseString(
    """
      |akka.remote.artery.canonical.port = 2552
    """.stripMargin
  ).withFallback(ConfigFactory.load("avroserializer"))

  val system = ActorSystem("RemoteSystem",config)
  val actor = system.actorOf(Props[SimpleActor],"simpleActor")


}

