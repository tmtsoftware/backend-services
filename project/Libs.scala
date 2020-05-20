import sbt._

object Libs {
  val ScalaVersion = "2.13.2"

  val `scalatest` = "org.scalatest" %% "scalatest" % "3.1.1" //Apache License 2.0
}

object AkkaHttp {
  private val Version = "10.2.0-M1" //all akka is Apache License 2.0

  val `akka-http-spray-json` = "com.typesafe.akka" %% "akka-http-spray-json" % Version
}

object ESW {
  val Version: String = "a202a1e38b"

  val `esw-testkit` = "com.github.tmtsoftware.esw" %% "esw-testkit" % Version
}
