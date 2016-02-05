resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

name := "event-stream-api"

version := scala.util.Properties.envOrElse("APP_VERSION", "snapshot")

scalaVersion := "2.11.6"

sbtVersion := "0.13.5"

scalacOptions ++= Seq("-Xfatal-warnings", "-feature")

val unfilteredLibraryVersion = "0.8.4"

resolvers += "gphat" at "https://raw.github.com/gphat/mvn-repo/master/releases/"

libraryDependencies ++= Seq(
  "net.databinder"                         %% "unfiltered-netty-server"  % unfilteredLibraryVersion,
  "net.databinder"                         %% "unfiltered-directives"    % unfilteredLibraryVersion,
  "net.databinder"                         %% "unfiltered-filter"        % unfilteredLibraryVersion,
  "io.argonaut"                            %% "argonaut"                 % "6.1-M4",
  "io.argonaut"                            %% "argonaut-unfiltered"      % "6.0.4",
  "commons-lang"                           %  "commons-lang"             % "2.6",
  "org.spire-math"                         %% "cats"                     % "0.3.0",
  "org.slf4j"                              %  "jul-to-slf4j"             % "1.7.7",
  "ch.qos.logback"                         %  "logback-classic"          % "1.1.2",
  "com.typesafe.slick"                     %% "slick"                    % "2.1.0",
  "postgresql"                             %  "postgresql"               % "9.1-901.jdbc4",
  "com.chuusai"                            %% "shapeless"                % "2.1.0",
  "org.specs2"                             %% "specs2"                   % "2.4.15" % "test"
)


mainClass in Compile := Some("app.Main") //Used in Universal packageBin

mainClass in (Compile, run) := Some("app.infrastructure.Dev") //Used from normal sbt
