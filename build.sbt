ThisBuild / version := "0.3.0"
ThisBuild / scalaVersion := "3.2.1"

lazy val root = crossProject(JSPlatform, JVMPlatform, NativePlatform).withoutSuffixFor(JVMPlatform)
  .aggregate(core, dom)
  .settings(
    name := "Momentum",
    organization := "be.adamv",
    idePackagePrefix := Some("be.adamv.momentum"),
    scalaJSUseMainModuleInitializer := true,
    publish / skip := true
  )


lazy val core = crossProject(JSPlatform, JVMPlatform, NativePlatform).withoutSuffixFor(JVMPlatform)
  .in(file("core"))
  .settings(
      name := "Momentum-core",
      organization := "be.adamv",
      idePackagePrefix := Some("be.adamv.momentum"),
      libraryDependencies += "org.scalameta" %%% "munit" % "1.0.0-M7" % Test,
      scalaJSUseMainModuleInitializer := true,
      publishTo := Some(Resolver.file("local-ivy", file("~")))
  )


lazy val dom = crossProject(JSPlatform).crossType(CrossType.Pure).withoutSuffixFor(JSPlatform)
  .in(file("dom"))
  .dependsOn(core % "compile->compile;test->test;provided->provided")
  .settings(
      name := "Momentum-dom",
      organization := "be.adamv",
      idePackagePrefix := Some("be.adamv.momentum"),
      libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.0.0",
      scalaJSUseMainModuleInitializer := true,
  )
