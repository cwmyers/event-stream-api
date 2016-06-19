resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

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

val circeVersion = "0.4.1"

resolvers += "cwmyers" at "https://raw.github.com/cwmyers/mvn-repo/master/releases/"

//addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)


libraryDependencies ++= Seq(
  "net.databinder"                         %% "unfiltered-netty-server"  % unfilteredLibraryVersion,
  "net.databinder"                         %% "unfiltered-directives"    % unfilteredLibraryVersion,
  "net.databinder"                         %% "unfiltered-filter"        % unfilteredLibraryVersion,
  "com.github.mpilquist"                   %% "simulacrum"               % "0.7.0",
  "io.github.cwmyers"                      %% "wrap"                     % "0.0.1",
  "commons-lang"                           %  "commons-lang"             % "2.6",
  "org.typelevel"                          %% "cats"                     % "0.4.1",
  "org.slf4j"                              %  "jul-to-slf4j"             % "1.7.7",
  "ch.qos.logback"                         %  "logback-classic"          % "1.1.2",
  "com.typesafe.slick"                     %% "slick"                    % "2.1.0",
  "postgresql"                             %  "postgresql"               % "9.1-901.jdbc4",
  "com.chuusai"                            %% "shapeless"                % "2.2.5",
  "io.circe"                               %% "circe-core"               % circeVersion,
  "io.circe"                               %% "circe-generic"            % circeVersion,
  "io.circe"                               %% "circe-parser"             % circeVersion,
  "org.specs2"                             %% "specs2"                   % "2.4.15" % "test"
)


mainClass in Compile := Some("app.Main") //Used in Universal packageBin

mainClass in (Compile, run) := Some("app.infrastructure.Dev") //Used from normal sbt
