package org.tmt

import csw.framework.deploy.containercmd.ContainerCmd
import csw.prefix.models.Subsystem.ESW
import org.tmt.utils.IOUtils

object ContainerCmdApp extends App {

  private def filterConfPath(commandArgs: Array[String]) = {
    commandArgs.map {
      case arg: String if arg.contains(".conf") =>
        IOUtils.writeResourceToFile(arg).toString
      case x => x
    }
  }

  ContainerCmd.start("ContainerCmdApp", ESW, filterConfPath(args))
}
