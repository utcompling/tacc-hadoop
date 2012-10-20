import AssemblyKeys._

assemblySettings

seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)


name := "tacc-scoobi"

version := "0.0.1"

scalaVersion := "2.9.2"

  
resolvers ++= Seq(
  "Sonatype-snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "cloudera" at "https://repository.cloudera.com/content/repositories/releases",
  "apache" at "https://repository.apache.org/content/repositories/releases",
  "scoobi" at "http://nicta.github.com/scoobi/releases")

libraryDependencies ++= Seq()

scalacOptions ++= Seq("-deprecation", "-Ydependent-method-types", "-unchecked")


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


mainClass in oneJar := None


