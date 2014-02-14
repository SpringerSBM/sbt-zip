package at.danisgre.sbt.zip

import sbt.TaskKey
import sbt.Keys._
import sbt.Configuration

object DependentProjects {

  lazy val dependentProjectsSettings = (allDependentConfigurations <<= allDependentConfigurationsTask)

  val allDependentConfigurations = TaskKey[Set[Configuration]]("allDependentConfigurations", "All the dependent configs")

  private def allDependentConfigurationsTask = {
    configuration map allDependentConfigurationsFor
  }

  private def allDependentConfigurationsFor(config: Configuration): Set[Configuration] = {
    config.extendsConfigs.flatMap(allDependentConfigurationsFor).toSet + config
  }
}
