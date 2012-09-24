import AssemblyKeys._

assemblySettings

name := "tacc-scoobi"

version := "0.0.1"

scalaVersion := "2.9.2"

libraryDependencies ++= Seq(
  "com.nicta" %% "scoobi" % "0.4.0")

scalacOptions ++= Seq("-Ydependent-method-types", "-deprecation")

resolvers ++= Seq(
  "Sonatype-snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "scoobi" at "http://nicta.github.com/scoobi/releases")

jarName in assembly := "tacc-scoobi-assembly.jar"

mainClass in assembly := None

test in assembly := {}


mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case x => {
      val oldstrat = old(x)
      if (oldstrat == MergeStrategy.deduplicate) MergeStrategy.first
      else oldstrat
    }
  }
}

