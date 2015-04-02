import com.typesafe.sbt.SbtNativePackager.packageArchetype

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

packageArchetype.java_application

name := "interpreter-app"

version := scala.util.Properties.envOrElse("APP_VERSION", "snapshot")

scalaVersion := "2.11.5"

sbtVersion := "0.13.5"

val unfilteredLibraryVersion = "0.8.4"

resolvers += "gphat" at "https://raw.github.com/gphat/mvn-repo/master/releases/"

libraryDependencies ++= Seq(
  "net.databinder"                         %% "unfiltered-netty-server"  % unfilteredLibraryVersion,
  "net.databinder"                         %% "unfiltered-directives"    % unfilteredLibraryVersion,
  "net.databinder"                         %% "unfiltered-filter"        % unfilteredLibraryVersion,
  "wabisabi"                               %% "wabisabi"                 % "2.0.14",
  "io.argonaut"                            %% "argonaut"                 % "6.1-M4",
  "io.argonaut"                            %% "argonaut-unfiltered"      % "6.0.4",
  "com.google.guava"                       %  "guava"                    % "18.0",
  "commons-lang"                           %  "commons-lang"             % "2.6",
  "org.slf4j"                              %  "jul-to-slf4j"             % "1.7.7",
  "ch.qos.logback"                         %  "logback-classic"          % "1.1.2",
  "com.chuusai"                            %% "shapeless"                % "2.1.0",
  "org.specs2"                             %% "specs2"                   % "2.4.15" % "test",
  "org.elasticsearch"                      %  "elasticsearch"            % "1.4.2"  % "test",
  "org.apache.lucene"                      % "lucene-expressions"        % "4.10.3" % "test",
  "org.codehaus.groovy"                    % "groovy-all"                % "2.3.2"  % "test"
)


mainClass in Compile := Some("app.Main") //Used in Universal packageBin

mainClass in (Compile, run) := Some("infrastructure.Dev") //Used from normal sbt
