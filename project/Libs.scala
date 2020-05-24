import sbt._

object Libs {
  val ScalaVersion = "2.13.2"

  val `scalatest` = "org.scalatest" %% "scalatest" % "3.1.1" //Apache License 2.0
}

object ESW {
  val Version: String = "0.1.0-SNAPSHOT"

  val `esw-testkit` = "com.github.tmtsoftware.esw" %% "esw-testkit" % Version
}
