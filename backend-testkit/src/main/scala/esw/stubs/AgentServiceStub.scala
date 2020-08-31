package esw.stubs

import java.nio.file.Path

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import csw.location.api.models.Connection
import csw.location.api.scaladsl.LocationService
import csw.prefix.models.Prefix
import esw.agent.service.api.AgentServiceApi
import esw.agent.service.api.models.{KillResponse, Killed, SpawnResponse, Spawned}
import esw.agent.service.app.AgentServiceWiring
import esw.ocs.testkit.utils.LocationUtils

import scala.concurrent.Future

class AgentServiceStubImpl extends AgentServiceApi {
  override def spawnSequenceManager(
      agentPrefix: Prefix,
      obsModeConfigPath: Path,
      isConfigLocal: Boolean,
      version: Option[String]
  ): Future[SpawnResponse] = Future.successful(Spawned)

  override def spawnSequenceComponent(
      agentPrefix: Prefix,
      componentName: String,
      version: Option[String]
  ): Future[SpawnResponse] = Future.successful(Spawned)

  override def killComponent(connection: Connection): Future[KillResponse] = Future.successful(Killed)
}

class AgentServiceStub(val locationService: LocationService, _actorSystem: ActorSystem[SpawnProtocol.Command])
    extends LocationUtils {
  private var agentServiceWiring: Option[AgentServiceWiring]            = _
  override implicit def actorSystem: ActorSystem[SpawnProtocol.Command] = _actorSystem
  def spawnMockAgentService(): AgentServiceWiring = {
    val wiring = new AgentServiceWiring() {
      override lazy val actorSystem: ActorSystem[SpawnProtocol.Command] = _actorSystem
      override lazy val agentService: AgentServiceApi                   = new AgentServiceStubImpl()
    }
    agentServiceWiring = Some(wiring)
    wiring.start()
    wiring
  }

  def shutdown() = {
    agentServiceWiring.foreach(_.stop().futureValue)
  }
}
