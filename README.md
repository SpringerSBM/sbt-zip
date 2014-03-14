sbt-zip
=======

An SBT plugin which creates a zip with all of your dependencies in it. The idea is that this is a simple way of distributing your application.

You use it by adding the `zipSettings` to the configuration what want to zip (such as `Test` or `Compile`)

All of your dependencies are put into the `lib/` folder. This includes the 'main' `packageBin` for the configuration.

Other files can be added by adding to the zipExtraFiles setting.

In order for dependencies to be resolved correctly, you will need to add you your build:

```scala
exportJars in ThisBuild := true
```

example usage:
```scala
import at.danisgre.sbt.zip.Zip._

lazy val zipSettings = 
    zipTaskForConfig(Test) ++
    (zipExtraFiles in Test := (baseDir / "ops" / "config" ** "*") pair flatRebase("config"))
```
