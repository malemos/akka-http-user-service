val versao = "0.1"
val projeto = "api-rest"
val ingestion = "br.com.vvdatalab.$projeto"
val versaoScala = "2.12.8"
val akkaHttp = "10.1.0"
val akka = "2.5.11"
val circe = "0.9.2"
val akkaHttpCirce = "1.20.0"
val reactiveMongo = "0.16.5"
val logback = "1.2.3"
val kamon = "1.1.0"

val scalaTest = "3.0.5"
val scalaMock = "4.2.0-SNAPSHOT"

name := projeto
version := "1.0"
scalaVersion := versaoScala
organization := ingestion

javacOptions in compile ++= Seq("-target", "8", "-source", "8")

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.3.3",
  "com.typesafe.akka" %% "akka-http" % akkaHttp,
  "io.circe" %% "circe-core" % circe,
  "io.circe" %% "circe-generic" % circe,
  "io.circe" %% "circe-parser" % circe,
  "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirce,
  "com.typesafe.akka" %% "akka-stream" % akka,
  "com.typesafe.akka" %% "akka-slf4j" % akka,
  "ch.qos.logback" % "logback-classic" % logback,
  "org.reactivemongo" %% "reactivemongo" % reactiveMongo,
  "io.kamon" %% "kamon-core" % kamon,
  "io.kamon" %% "kamon-akka-http-2.5" % kamon,
  "com.typesafe.akka" %% "akka-actor" % akka
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttp % "test",
  "org.scalatest" %% "scalatest" % scalaTest % "test",
  "org.scalamock" %% "scalamock" % scalaMock % "test"
)

resolvers ++= Seq(
  "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository",
  "ViaVarejo" at "http://nexus.viavarejo.com.br/repository/hortonworks-group/",
  "JBoss repository" at "http://repository.jboss.org/nexus/content/groups/public/",
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

enablePlugins(JavaAppPackaging, AshScriptPlugin, JavaAgent)

assemblyJarName := "api-rest-looqbox.jar"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case "reference.conf" => MergeStrategy.concat
  case _ => MergeStrategy.first
}

mappings in Universal := {
  val universalMappings = (mappings in Universal).value
  val fatJar = (assembly in Compile).value
  val filtered = universalMappings filter {
    case (file, name) => !name.endsWith(".jar")
  }
  filtered :+ (fatJar -> ("lib/" + fatJar.getName))
}

// the bash scripts classpath only needs the fat jar
scriptClasspath := Seq((assemblyJarName).value)
