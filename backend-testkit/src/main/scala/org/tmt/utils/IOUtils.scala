package org.tmt.utils

import java.nio.file.{Files, Path, StandardCopyOption}

object IOUtils {

  def writeResourceToFile(resourceName: String, suffix: String = ".conf"): Path = {
    val configStream = getClass.getClassLoader.getResourceAsStream(resourceName)
    val tmpFile      = Files.createTempFile(resourceName, suffix)
    Files.copy(configStream, tmpFile, StandardCopyOption.REPLACE_EXISTING)
    tmpFile
  }
}
