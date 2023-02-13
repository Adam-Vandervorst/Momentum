ThisBuild / version := "0.2.2"
ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "Momentum",
    organization := "be.adamv",
    idePackagePrefix := Some("be.adamv.momentum"),
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    publishTo := Some(Resolver.file("local-ivy", file("~")))
  )
