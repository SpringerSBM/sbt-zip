package at.danisgre.sbt.zip

import sbt._
import Keys._
import DependentProjects._
import BuildDependencies._

object Zip extends Plugin {

  lazy val zipSettings =
    dependentProjectsSettings ++
    buildDependenciesSettings ++
    ((artifactPath in zip)    :=  asZip((artifactPath in packageBin).value)) ++
    (zip                      <<= zipTask) ++
    (zipExtraFiles            :=  Seq.empty)

  val zip                     = TaskKey[File]("zip", "Produces a zip with all dependencies of a configuration")
  val zipExtraFiles           = SettingKey[Seq[(File, String)]]("zipExtraFiles", "Extra files (and their paths) which should be added to the zip")

  def zipTaskForConfig(config: Configuration) = {
    inConfig(config)(zipSettings)
  }

  def zipTaskForConfigWithExtraFiles(config: Configuration, files: Seq[(File, String)]) = {
    inConfig(config)(zipSettings) ++
    ((zipExtraFiles in config) := files)
  }

  private def zipTask = {
    (allTheJars, artifactPath in zip, zipExtraFiles, streams) map {
      (packages, outputZip, extraFiles, streams) => {
        val libs = packages.toSeq.get pair flatRebase("lib")
        streams.log.info(s"Zipping ${outputZip.getAbsolutePath} ...")
        IO.zip(libs ++ extraFiles, outputZip)
        streams.log.info("Done zipping.")
        outputZip
      }
    }
  }

  private def asZip(jarFile: File): File = {
    file(jarFile.getAbsolutePath.replace(".jar", ".zip"))
  }
}

