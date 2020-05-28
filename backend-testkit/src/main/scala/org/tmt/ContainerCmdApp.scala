package org.tmt

import csw.framework.deploy.containercmd.ContainerCmd
import csw.prefix.models.Subsystem.ESW
import org.tmt.utils.IOUtils

object ContainerCmdApp extends App {

  private val updatedArgs = args.map {
    case arg: String if arg.contains(".conf") => IOUtils.writeResourceToFile(arg).toString
    case arg                                    => arg
  }

  ContainerCmd.start("ContainerCmdApp", ESW, updatedArgs)
}
