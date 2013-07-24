import AssemblyKeys._

assemblySettings

seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)

name := "tacc-hadoop"

version := "0.0.3"

scalaVersion := "2.9.2"

resolvers ++= Seq(
  "Sonatype-snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "cloudera" at "https://repository.cloudera.com/content/repositories/releases"
)

scalacOptions ++= Seq("-deprecation", "-Ydependent-method-types", "-optimize", "-unchecked")

jarName in assembly := "tacc-hadoop-assembly.jar"

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
