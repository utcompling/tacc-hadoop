import sbt._
import Keys._

object TaccScoobiBuild extends Build {

  lazy val main = Project("tacc-scoobi", file(".")) dependsOn(scoobi)

  lazy val scoobi = Project("scoobi", file("scoobi"))

}

