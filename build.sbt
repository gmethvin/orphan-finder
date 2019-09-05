inThisBuild(
  Seq(
    organization := "io.methvin",
    organizationName := "Greg Methvin",
    startYear := Some(2018),
    licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
    homepage in ThisBuild := Some(url("https://github.com/gmethvin/orphan-finder")),
    scmInfo in ThisBuild := Some(
      ScmInfo(url("https://github.com/gmethvin/orphan-finder"), "scm:git@github.com:gmethvin/orphan-finder.git")
    ),
    developers := List(
      Developer("gmethvin", "Greg Methvin", "greg@methvin.net", new URL("https://github.com/gmethvin"))
    ),
    crossScalaVersions := Seq("2.13.0", "2.12.9"),
    scalaVersion := crossScalaVersions.value.head,
    scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint"),
    dynverSonatypeSnapshots := true,
    scalafmtOnCompile := true
  )
)

lazy val noPublishSettings =
  Seq(PgpKeys.publishSigned := {}, publish := {}, publishLocal := {}, publishArtifact := false, skip in publish := true)

lazy val root = (project in file("."))
  .settings(noPublishSettings)
  .aggregate(`orphan-finder`, demo)

lazy val `orphan-finder` = (project in file("plugin"))
  .settings(
    publishTo := sonatypePublishTo.value,
    libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value,
    scalacOptions += "-Xfatal-warnings",
    crossVersion := CrossVersion.patch
  )
  .enablePlugins(AutomateHeaderPlugin)

lazy val demo = (project in file("demo"))
  .settings(noPublishSettings)
  .settings(scalacOptions in Compile ++= {
    val jar = (Keys.`package` in (`orphan-finder`, Compile)).value
    Seq(
      s"-Xplugin:${jar.getAbsolutePath}",
      s"-P:orphan-finder:class:${classOf[scala.concurrent.Future[_]].getName}",
      s"-Jdummy=${jar.lastModified}"
    )
  })
