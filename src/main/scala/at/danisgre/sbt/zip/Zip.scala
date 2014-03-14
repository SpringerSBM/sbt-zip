package at.danisgre.sbt.zip

import sbt._
import Keys._

object Zip extends Plugin {

  lazy val zipSettings =
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
    (fullClasspath, artifactPath in zip, zipExtraFiles, streams) map {
      (fullClasspath, artifactPath, extraFiles, streams) => {
        val libs = fullClasspath.files.get pair flatRebase("lib")
        streams.log.info(s"Zipping ${artifactPath.getAbsolutePath} ...")
        IO.zip(libs ++ extraFiles, artifactPath)
        streams.log.info("Done zipping.")
        artifactPath
      }
    }
  }

  private def asZip(jarFile: File): File = {
    file(jarFile.getAbsolutePath.replace(".jar", ".zip"))
  }
}

