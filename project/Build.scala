import sbt._
import Keys._
import Tests._

object SlickSampleBuild extends Build {

  val repoKind = SettingKey[String]("repo-kind", "Maven repository kind (\"snapshots\" or \"releases\")")
  lazy val sharedSettings = Seq(
    resolvers += Resolver.sonatypeRepo("snapshots"),
    scalacOptions ++= List("-deprecation", "-feature"),
    libraryDependencies += "org.slf4j" % "slf4j-api" % "1.6.4",
    libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.10.0-M7",
    scalaVersion := "2.10.0-M7",
    // Add scala-compiler dependency for scala.reflect.internal
    // libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _),
    logBuffered := false,
    repoKind <<= (version)(v => if(v.trim.endsWith("SNAPSHOT")) "snapshots" else "releases")
  )

  lazy val macrosProject = Project(id = "stubMacros", base = file("stubmacros"),
  settings = Project.defaultSettings ++ sharedSettings ) 

  lazy val slickExampleProject = Project(id = "slickExampleProject", base = file("."),
    settings = Project.defaultSettings ++ sharedSettings ) dependsOn (macrosProject)

}
