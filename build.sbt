name := "AkkaSerialization"

version := "0.1"

scalaVersion := "2.12.6"

val akkaVersion      = "2.5.16"
val akkaHttpVersion  = "10.1.5"
val scalaTestVersion = "3.0.5"

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

// (optional) If you need scalapb/scalapb.proto or anything from
// google/protobuf/*.proto
libraryDependencies += "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"

libraryDependencies ++= Seq (
  "com.typesafe.akka"      %% "akka-actor"           % akkaVersion,
  "com.typesafe.akka"      %% "akka-slf4j"           % akkaVersion,
  "com.typesafe.akka"      %% "akka-http"            % akkaHttpVersion,
  "com.typesafe.akka"      %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka"      %% "akka-stream"          % akkaVersion,
  "io.spray"               %% "spray-json"           % "1.3.3",
  "com.typesafe.akka"      %% "akka-remote"          % akkaVersion,
  "com.typesafe.akka"      %% "akka-cluster"         % akkaVersion,
  "com.typesafe.akka"      %% "akka-cluster-tools"   % akkaVersion,
  "com.typesafe.akka"      %% "akka-persistence"     % akkaVersion,
  "com.typesafe.akka"      %% "akka-persistence-query" % akkaVersion,
  "com.typesafe.akka"      %% "akka-stream-testkit"  % akkaVersion,
  "com.typesafe.akka"      %% "akka-testkit"         % akkaVersion,
  "com.github.romix.akka"  %% "akka-kryo-serialization" % "0.5.1",
  "com.sksamuel.avro4s"    %% "avro4s-core"          % "2.0.4",
  "com.google.protobuf"    % "protobuf-java"         % "3.8.0",
  "org.scalatest"          %% "scalatest"            % scalaTestVersion
)