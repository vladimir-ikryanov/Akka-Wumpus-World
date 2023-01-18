name := "Akka Wumpus World"
version := "1.0"
scalaVersion := "2.13.0"
resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"
val AkkaVersion = "2.7.0"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test)