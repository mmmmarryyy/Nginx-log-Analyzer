import Dependencies.{Versions => _, _}

lazy val analyzer = project
  .settings(
    name := "analyzer",
    scalaVersion := Versions.scala3,
    libraryDependencies ++= Seq(scalaTest, scalastic),
    libraryDependencies += "com.github.scopt" %% "scopt" % "4.1.0"
)
