addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.9.0")


resolvers += Resolver.url(
  "sbt-plugin-releases", 
  new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/")
)(Resolver.ivyStylePatterns)

addSbtPlugin("com.github.retronym" % "sbt-onejar" % "0.8")


addSbtPlugin("com.typesafe.sbt" % "sbt-start-script" % "0.8.0")
