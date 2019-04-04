addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.3")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.2.4")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.9")

resolvers += Resolver.bintrayRepo("kamon-io", "sbt-plugins")
addSbtPlugin("io.kamon" % "sbt-aspectj-runner" % "1.1.0")
addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.4")

dependencyOverrides ++= Seq(
  "org.codehaus.plexus" % "plexus-utils" % "3.0.17",
  "com.google.guava" % "guava" % "20.0"
)
