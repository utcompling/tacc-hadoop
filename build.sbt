import AssemblyKeys._

assemblySettings

seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)

name := "tacc-hadoop"

version := "0.0.2"

scalaVersion := "2.9.2"

resolvers ++= Seq(
  "Sonatype-snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "cloudera" at "https://repository.cloudera.com/content/repositories/releases",
  "apache" at "https://repository.apache.org/content/repositories/releases",
  "scoobi" at "http://nicta.github.com/scoobi/releases",
  "gwtwiki" at "http://gwtwiki.googlecode.com/svn/maven-repository/"
)

libraryDependencies ++= Seq(
  "edu.umd" % "cloud9" % "1.3.5",
  "info.bliki.wiki" % "bliki-core" % "3.0.16",
  "commons-lang" % "commons-lang" % "2.6"
)

scalacOptions ++= Seq("-deprecation", "-Ydependent-method-types", "-unchecked")

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
