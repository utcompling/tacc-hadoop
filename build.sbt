import AssemblyKeys._

assemblySettings

name := "tacc-scoobi"

version := "0.0.1"

scalaVersion := "2.9.2"

resolvers ++= Seq(
  "Sonatype-snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "cloudera" at "https://repository.cloudera.com/content/repositories/releases",
  "apache" at "https://repository.apache.org/content/repositories/releases",
  "scoobi" at "http://nicta.github.com/scoobi/releases")

libraryDependencies ++= Seq()
  //"com.nicta" %% "scoobi" % "0.4.0")

scalacOptions ++= Seq("-Ydependent-method-types", "-deprecation")

jarName in assembly := "tacc-scoobi-assembly.jar"

mainClass in assembly := None

test in assembly := {}

seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)

mainClass in oneJar := Some("dhg.tacc.WordCountMaterialize")

scalacOptions ++= Seq("-deprecation", "-Ydependent-method-types", "-unchecked")

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case x => {
      val oldstrat = old(x)
      if (oldstrat == MergeStrategy.deduplicate) MergeStrategy.first
      else oldstrat
    }
  }
}

