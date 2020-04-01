name := "event-stream-api"

version := scala.util.Properties.envOrElse("APP_VERSION", "latest")

scalaVersion := "2.13.1"

sbtVersion := "1.3.9"

scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-Ywarn-unused",
  "-Xlint",
  "-feature",
  "-deprecation",
  "-unchecked",
  "-feature",
  "-language:higherKinds"
//    "-Xlog-implicits"
)

val unfilteredLibraryVersion = "0.10.0-M7"
val circeVersion             = "0.12.3"
val specsVersion             = "4.8.3"
val catsVersion              = "2.1.0"

resolvers += "cwmyers" at "https://raw.github.com/cwmyers/mvn-repo/master/releases/"

//addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)


libraryDependencies ++= Seq(
  "ws.unfiltered"                         %% "unfiltered-filter"        % unfilteredLibraryVersion,
  "ws.unfiltered"                         %% "unfiltered-directives"    % unfilteredLibraryVersion,
  "ws.unfiltered"                         %% "unfiltered-filter"        % unfilteredLibraryVersion,
  "ws.unfiltered"                         %% "unfiltered-netty-server"  % unfilteredLibraryVersion,
  "commons-lang"                           %  "commons-lang"             % "2.6",
  "org.typelevel"                          %% "cats-core"                % catsVersion,
  "org.typelevel"                          %% "cats-free"                % catsVersion,
  "org.typelevel"                          %% "mouse"                    % "0.24",
  "org.slf4j"                              %  "jul-to-slf4j"             % "1.7.7",
  "ch.qos.logback"                         %  "logback-classic"          % "1.1.2",
  "com.typesafe.slick"                     %% "slick"                    % "3.3.2",
  "com.github.tminglei"                    %% "slick-pg"                 % "0.19.0",
  "postgresql"                             %  "postgresql"               % "9.1-901.jdbc4",
  "io.circe"                               %% "circe-core"               % circeVersion,
  "io.circe"                               %% "circe-generic"            % circeVersion,
  "io.circe"                               %% "circe-parser"             % circeVersion,
  "org.specs2"                             %% "specs2-core"              % specsVersion       % "test",
  "org.specs2"                             %% "specs2-scalacheck"        % specsVersion       % "test"
)


mainClass in Compile := Some("app.Main") //Used in Universal packageBin

mainClass in (Compile, run) := Some("app.infrastructure.Dev") //Used from normal sbt

//compileInputs in (Compile, compile) <<=
//  (compileInputs in (Compile, compile)) dependsOn (scalafmt in Compile)


enablePlugins(JavaServerAppPackaging, DockerPlugin)

// Add your own repo here
dockerRepository := Some("docker.pkg.github.com/cwmyers")

dockerBaseImage := scala.util.Properties.envOrElse("DOCKER_IMAGE", "openjdk:latest")

packageName in Docker := scala.util.Properties.envOrElse("DOCKER_PACKAGE_NAME", packageName.value)
