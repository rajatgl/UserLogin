name := "LoginRegistration"
version := "0.1"
scalaVersion := "2.12.2"

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.32",
  "com.typesafe.akka" %% "akka-stream" % "2.5.32",
  "com.typesafe.akka" %% "akka-http" % "10.2.2",
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0",
  "com.lightbend.akka" %% "akka-stream-alpakka-mongodb" % "2.0.2",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.2",
  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5",
  "com.nimbusds" % "nimbus-jose-jwt" % "9.3",
  "org.scalatest" %% "scalatest" % "3.2.2" % "test"

)