package org.tmt

import java.nio.file.Path

import akka.actor.CoordinatedShutdown
import caseapp.RemainingArgs
import com.typesafe.config.ConfigFactory
import csw.logging.client.scaladsl.LoggingSystemFactory
import csw.network.utils.Networks
import csw.testkit.scaladsl.CSWService.AlarmServer
import esw.http.core.commons.EswCommandApp
import esw.ocs.testkit.Service.{AAS, Gateway, SequenceManager, WrappedCSWService}
import esw.ocs.testkit.{EswTestKit, Service}
import esw.stubs.{GatewayStub, SequenceManagerStub}
import org.tmt.TSServicesCommands._

import scala.util.control.NonFatal

object BackendService extends EswCommandApp[TSServicesCommands] {
  override def appName: String  = getClass.getSimpleName.dropRight(1) // remove $ from class name
  override def progName: String = "backend-testkit"

  override def run(options: TSServicesCommands, remainingArgs: RemainingArgs): Unit =
    options match {
      case Start(services, commandRoles, alarmConf) => run(services, commandRoles, alarmConf)
    }

  private def run(services: List[Service], commandRoles: Path, alarmConf: String): Unit = {
    val servicesWithoutGateway = services.filterNot(_ == Gateway)
    val eswTestKit: EswTestKit = new EswTestKit(servicesWithoutGateway: _*) {}

    var gatewayWiring: Option[GatewayStub]                 = None
    var sequenceManagerWiring: Option[SequenceManagerStub] = None

    def shutdown(): Unit = {
      gatewayWiring.foreach(_.shutdownGateway())
      sequenceManagerWiring.foreach(_.shutdown())
      eswTestKit.afterAll()
    }

    try {
      import eswTestKit._
      LoggingSystemFactory.start(progName, "0.1.0-SNAPSHOT", Networks().hostname, actorSystem)
      import frameworkTestKit.frameworkWiring.alarmServiceFactory

      def initDefaultAlarms() = {
        val config            = ConfigFactory.parseResources(alarmConf)
        val alarmAdminService = alarmServiceFactory.makeAdminApi(locationService)
        alarmAdminService.initAlarms(config, reset = true).futureValue
      }

      eswTestKit.beforeAll()
      if (services.contains(WrappedCSWService(AlarmServer))) initDefaultAlarms()
      if (services.contains(Gateway)) {
        val gateway = new GatewayStub(locationService, actorSystem)
        gatewayWiring = Some(gateway)
        gateway.spawnMockGateway(services.contains(AAS), commandRoles)
      }
      if (services.contains(SequenceManager)) {
        val sequenceManagerStub = new SequenceManagerStub(locationService, actorSystem)
        sequenceManagerWiring = Some(sequenceManagerStub)
        sequenceManagerStub.spawnMockSm()
      }
      CoordinatedShutdown(actorSystem).addJvmShutdownHook(shutdown())
    }
    catch {
      case NonFatal(e) => shutdown(); throw e
    }
  }

}
