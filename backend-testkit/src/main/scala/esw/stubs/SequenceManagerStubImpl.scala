package esw.stubs

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import csw.location.api.models.ComponentId
import csw.location.api.models.ComponentType.Sequencer
import csw.location.api.scaladsl.LocationService
import csw.prefix.models.Subsystem.ESW
import csw.prefix.models.{Prefix, Subsystem}
import esw.ocs.api.models.ObsMode
import esw.ocs.testkit.utils.LocationUtils
import esw.sm.api.SequenceManagerApi
import esw.sm.api.models.ProvisionConfig
import esw.sm.api.protocol.{
  AgentStatusResponse,
  ConfigureResponse,
  GetRunningObsModesResponse,
  ProvisionResponse,
  RestartSequencerResponse,
  ShutdownSequenceComponentResponse,
  ShutdownSequencersResponse,
  StartSequencerResponse
}

import scala.concurrent.Future

class SequenceManagerStubImpl(val locationService: LocationService, _actorSystem: ActorSystem[SpawnProtocol.Command])
    extends LocationUtils
    with SequenceManagerApi {
  override implicit def actorSystem: ActorSystem[SpawnProtocol.Command] = _actorSystem

  override def configure(obsMode: ObsMode): Future[ConfigureResponse] =
    Future.successful(ConfigureResponse.Success(ComponentId(Prefix(ESW, "darknight"), Sequencer)))

  override def provision(config: ProvisionConfig): Future[ProvisionResponse] = ???

  override def getRunningObsModes: Future[GetRunningObsModesResponse] = ???

  override def startSequencer(subsystem: Subsystem, obsMode: ObsMode): Future[StartSequencerResponse] = ???

  override def restartSequencer(subsystem: Subsystem, obsMode: ObsMode): Future[RestartSequencerResponse] = ???

  override def shutdownSequencer(subsystem: Subsystem, obsMode: ObsMode): Future[ShutdownSequencersResponse] = ???

  override def shutdownSubsystemSequencers(subsystem: Subsystem): Future[ShutdownSequencersResponse] = ???

  override def shutdownObsModeSequencers(obsMode: ObsMode): Future[ShutdownSequencersResponse] = ???

  override def shutdownAllSequencers(): Future[ShutdownSequencersResponse] = ???

  override def shutdownSequenceComponent(prefix: Prefix): Future[ShutdownSequenceComponentResponse] = ???

  override def shutdownAllSequenceComponents(): Future[ShutdownSequenceComponentResponse] = ???

  override def getAgentStatus: Future[AgentStatusResponse] = ???
}
