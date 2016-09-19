import org.scalafmt.sbt.ScalaFmtPlugin.autoImport._

name := "event-stream-api"

version := scala.util.Properties.envOrElse("APP_VERSION", "snapshot")

scalaVersion := "2.11.8"

sbtVersion := "0.13.11"

scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-Ywarn-unused-import",
  "-Xlint",
  "-feature",
  "-deprecation",
  "-unchecked",
  "-feature",
  "-language:higherKinds"
//    "-Xlog-implicits"
)

val unfilteredLibraryVersion = "0.8.4"

val circeVersion = "0.5.1"

resolvers += "cwmyers" at "https://raw.github.com/cwmyers/mvn-repo/master/releases/"

//addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)


libraryDependencies ++= Seq(
  "net.databinder"                         %% "unfiltered-netty-server"  % unfilteredLibraryVersion,
  "net.databinder"                         %% "unfiltered-directives"    % unfilteredLibraryVersion,
  "net.databinder"                         %% "unfiltered-filter"        % unfilteredLibraryVersion,
  "com.github.mpilquist"                   %% "simulacrum"               % "0.7.0",
  "commons-lang"                           %  "commons-lang"             % "2.6",
  "org.typelevel"                          %% "cats"                     % "0.7.2",
  "com.github.benhutchison"                %% "mouse"                    % "0.4",
  "org.slf4j"                              %  "jul-to-slf4j"             % "1.7.7",
  "ch.qos.logback"                         %  "logback-classic"          % "1.1.2",
  "com.typesafe.slick"                     %% "slick"                    % "3.1.1",
  "com.github.tminglei"                    %% "slick-pg"                 % "0.14.3",
  "postgresql"                             %  "postgresql"               % "9.1-901.jdbc4",
  "io.circe"                               %% "circe-core"               % circeVersion,
  "io.circe"                               %% "circe-generic"            % circeVersion,
  "io.circe"                               %% "circe-parser"             % circeVersion,
  "io.circe"                               %% "circe-optics"             % circeVersion,
  "org.specs2"                             %% "specs2-core"              % "3.8.5"       % "test",
  "org.specs2"                             %% "specs2-scalacheck"        % "3.8.5"       % "test"
)


mainClass in Compile := Some("app.Main") //Used in Universal packageBin

mainClass in (Compile, run) := Some("app.infrastructure.Dev") //Used from normal sbt

scalafmtConfig in ThisBuild := Some(file(".scalafmt"))

compileInputs in (Compile, compile) <<=
  (compileInputs in (Compile, compile)) dependsOn (scalafmt in Compile)

