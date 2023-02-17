ThisBuild / version := "0.3.0"
ThisBuild / scalaVersion := "3.2.1"

lazy val root = crossProject(JSPlatform, JVMPlatform, NativePlatform).withoutSuffixFor(JVMPlatform)
  .in(file("."))
  .settings(
    name := "Momentum",
    organization := "be.adamv",
    idePackagePrefix := Some("be.adamv.momentum"),
    libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M7" % Test,
    scalaJSUseMainModuleInitializer := true,
    publishTo := Some(Resolver.file("local-ivy", file("~")))
  )
