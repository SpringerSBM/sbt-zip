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
    (zip                      <<= newZipTask) ++
    (newZipExtraFiles         :=  Seq.empty)

  val zip                     = TaskKey[File]("zip", "New zip task")
  val newZipExtraFiles        = SettingKey[Seq[(File, String)]]("newZipExtraFiles")

  private def asZip(jarFile: File): File = {
    file(jarFile.getAbsolutePath.replace(".jar", ".zip"))
  }

  private def newZipTask = {
    (allTheJars, (artifactPath in zip), newZipExtraFiles) map {
      (packages, outputZip, extraFiles) => {
        val libs = packages.toSeq.get pair flatRebase("lib")
        IO.zip(libs ++ extraFiles, outputZip)
        outputZip
      }
    }
  }
}

