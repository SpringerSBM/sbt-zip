package at.danisgre.sbt.zip

import sbt._
import sbt.Keys._
import at.danisgre.sbt.zip.DependentProjects._

object BuildDependencies {

  lazy val buildDependenciesSettings =
    (allPackageBins           <<= allPackageBinsTask) ++
    (allDependencyClasspaths  <<= allDependencyClasspathsTask) ++
    (allDependencyJars        <<= allDependencyJarsTask) ++
    (allTheJars               <<= allTheJarsTask)

  val allDependencyJars       = TaskKey[Set[File]]("allDependencyJars", "All the dependency jars")
  val allDependencyClasspaths = TaskKey[Set[Classpath]]("allDependencyClassPaths", "bob")
  val allPackageBins          = TaskKey[Set[File]]("allPackageBins", "All the dependent package binaries")
  val allTheJars              = TaskKey[Set[File]]("allTheJars", "")

  private lazy val allDependencyClasspathsTask = Def.taskDyn[Set[Classpath]] {
    dependencyClasspath.all(filterForConfigs(allDependentConfigurations.value)).map(_.toSet)
  }

  private lazy val allDependencyJarsTask = Def.taskDyn[Set[File]] {
    allDependencyClasspaths.map(classpaths => {
      classpaths.flatMap(_.files).toSet
    })
  }

  private lazy val allPackageBinsTask = Def.taskDyn[Set[File]] {
    packageBin.all(
      ScopeFilter(configurations = inConfigurations(allDependentConfigurations.value.toSeq:_*))
    ).map(_.toSet)
  }

  private lazy val allTheJarsTask = {
    (allPackageBins, allDependencyJars) map {
      (jars, depJars) => jars ++ depJars
    }
  }

  private def filterForConfigs(configs: Set[Configuration]): ScopeFilter = {
    ScopeFilter(configurations = inConfigurations(configs.toSeq:_*))
  }
}
