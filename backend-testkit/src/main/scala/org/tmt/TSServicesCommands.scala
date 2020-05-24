package org.tmt

import java.nio.file.Path

import caseapp.core.Error.UnrecognizedArgument
import caseapp.core.argparser.SimpleArgParser
import caseapp.{ExtraName, HelpMessage, _}
import csw.testkit.scaladsl.CSWService.{AlarmServer, ConfigServer, EventServer, LocationServer}
import esw.ocs.testkit.Service
import esw.ocs.testkit.Service.{AAS, Gateway}
import org.tmt.utils.IOUtils

sealed trait TSServicesCommands
object TSServicesCommands {

  private lazy val commandRolesPath = IOUtils.writeResourceToFile("commandRoles.conf")

  implicit val serviceParser: SimpleArgParser[Service] = {
    SimpleArgParser.from[Service]("service") {
      case "Location" => Right(LocationServer)
      case "Event"    => Right(EventServer)
      case "Alarm"    => Right(AlarmServer)
      case "Config"   => Right(ConfigServer)
      case "Gateway"  => Right(Gateway)
      case "AAS"      => Right(AAS)
      case unknown    => Left(UnrecognizedArgument(unknown))
    }
  }

  @CommandName("start")
  final case class Start(
      @HelpMessage("Service name e.g., Location, Gateway etc")
      @ExtraName("s")
      services: List[Service],
      @HelpMessage("path to command role from root directory")
      @ExtraName("r")
      commandRoles: Path = commandRolesPath
  ) extends TSServicesCommands
}