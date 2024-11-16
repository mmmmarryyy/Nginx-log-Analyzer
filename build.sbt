import Dependencies.{Versions => _, _}

lazy val analyzer = project
  .settings(
    name := "analyzer",
    scalaVersion := Versions.scala3,
    libraryDependencies ++= Seq(scalaTest, scalastic),
    libraryDependencies += "com.github.scopt" %% "scopt" % "4.1.0",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.5",
    libraryDependencies += "org.typelevel" %% "munit-cats-effect-3" % "1.0.3" % "test"
)
