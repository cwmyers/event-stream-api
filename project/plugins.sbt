resolvers += "rea-releases" at "http://nexus.delivery.realestate.com.au/nexus/content/repositories/releases/"

val reaSbtPluginVersion = "3.49"

addSbtPlugin("com.rea-group.rea-sbt-plugin" % "rea-sbt-plugin" % reaSbtPluginVersion)

resolvers ++= Seq(
  Resolver.url("scalasbt", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns),
  "ibiblio" at "http://mirrors.ibiblio.org/pub/mirrors/maven2"
)

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "0.6.2")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.4.0")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

scalaVersion := "2.10.4"

sbtVersion := "0.13.5"
