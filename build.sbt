import Dependencies.{Versions => _, _}

lazy val samples = project
  .settings(
    name := "samples",
    scalaVersion := Versions.scala3,
    libraryDependencies ++= Seq(scalaTest, scalastic)
  )

lazy val `seminar-1` = project.settings(scalaVersion := Versions.scala3)
