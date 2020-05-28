package org.tmt

import java.nio.file.Path

import akka.actor.CoordinatedShutdown
import caseapp.RemainingArgs
import com.typesafe.config.ConfigFactory
import csw.logging.client.scaladsl.LoggingSystemFactory
import csw.network.utils.Networks
import csw.testkit.scaladsl.CSWService.AlarmServer
import esw.http.core.commons.EswCommandApp
import esw.ocs.testkit.Service.{AAS, Gateway, WrappedCSWService}
import esw.ocs.testkit.{EswTestKit, Service}
import org.tmt.TSServicesCommands._

import scala.util.control.NonFatal

object BackendService extends EswCommandApp[TSServicesCommands] {
  override def appName: String  = getClass.getSimpleName.dropRight(1) // remove $ from class name
  override def progName: String = "backend-testkit"

  private lazy val eswTestKit: EswTestKit = new EswTestKit() {}
  import eswTestKit._

  override def run(options: TSServicesCommands, remainingArgs: RemainingArgs): Unit =
    options match {
      case Start(services, commandRoles, alarmConf) => run(services, commandRoles, alarmConf)
    }

  private def run(services: List[Service], commandRoles: Path, alarmConf: String): Unit =
    try {
      LoggingSystemFactory.start(progName, "0.1.0-SNAPSHOT", Networks().hostname, actorSystem)
      startServices(services, commandRoles, alarmConf)
      CoordinatedShutdown(actorSystem).addJvmShutdownHook(shutdown())
    }
    catch {
      case NonFatal(e) => shutdown(); throw e
    }

  private def startServices(services: List[Service], commandRoles: Path, alarmConf: String): Unit = {
    frameworkTestKit.start(Service.convertToCsw(services): _*)
    import frameworkTestKit.frameworkWiring.alarmServiceFactory

    services.foreach {
      case AAS     => startKeycloak()
      case Gateway => spawnGateway(services.contains(AAS), commandRoles)
      case WrappedCSWService(AlarmServer) => {
        val config            = ConfigFactory.parseResources(alarmConf)
        val alarmAdminService = alarmServiceFactory.makeAdminApi(locationService)
        alarmAdminService.initAlarms(config, reset = true).futureValue
      }
      case _ => ()
    }
  }

  private def shutdown(): Unit = eswTestKit.afterAll()
}
