package esw.stubs

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import akka.util.Timeout
import csw.command.client.messages.sequencer.SequencerMsg
import csw.location.api.models.AkkaLocation
import csw.location.api.scaladsl.LocationService
import csw.params.commands.CommandIssue.{IdNotAvailableIssue, UnsupportedCommandInStateIssue}
import csw.params.commands.CommandResponse.{Completed, Invalid, Started, SubmitResponse}
import csw.params.commands.{Sequence, SequenceCommand}
import csw.params.core.models.Id
import csw.time.core.models.UTCTime
import esw.ocs.api.SequencerApi
import esw.ocs.api.actor.messages.SequencerState
import esw.ocs.api.actor.messages.SequencerState.{Idle, InProgress, Loaded, Offline}
import esw.ocs.api.models.StepList
import esw.ocs.api.protocol._
import esw.ocs.testkit.utils.LocationUtils

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.duration.{DurationLong, FiniteDuration}

class SequencerServiceStubImpl(val locationService: LocationService, _actorSystem: ActorSystem[SpawnProtocol.Command])
    extends SequencerApi
    with LocationUtils {

  override implicit def actorSystem: ActorSystem[SpawnProtocol.Command] = _actorSystem

  private val delayDuration: FiniteDuration        = 5.seconds
  private val crm: mutable.Map[Id, SubmitResponse] = mutable.Map()

  private var sequenceOpt: Option[Sequence]               = None
  private var sequenceState: SequencerState[SequencerMsg] = Idle
  private var online: Boolean                             = true

  override def loadSequence(sequence: Sequence): Future[OkOrUnhandledResponse] = {
    val res = if (sequenceState == Idle) {
      sequenceState = Loaded
      sequenceOpt = Some(sequence)
      Ok
    }
    else Unhandled(sequenceState.toString, "LoadSequence")
    Future.successful(res)
  }

  override def startSequence(): Future[SubmitResponse] = {
    val runId = Id()
    val res = if (sequenceState == Loaded) {
      sequenceState = InProgress
      future(delayDuration, Completed(runId)).foreach(res => {
        sequenceState = Idle
        sequenceOpt = None
        crm.put(runId, res)
      })
      Started(runId)
    }
    else {
      Invalid(runId, UnsupportedCommandInStateIssue(s"unhandled msg in the $sequenceState"))
    }

    crm.put(runId, res)
    Future.successful(res)
  }

  override def getSequence: Future[Option[StepList]] = Future.successful(sequenceOpt.map(StepList(_)))

  override def add(commands: List[SequenceCommand]): Future[OkOrUnhandledResponse] = ???

  override def prepend(commands: List[SequenceCommand]): Future[OkOrUnhandledResponse] = ???

  override def replace(id: Id, commands: List[SequenceCommand]): Future[GenericResponse] = ???

  override def insertAfter(id: Id, commands: List[SequenceCommand]): Future[GenericResponse] = ???

  override def delete(id: Id): Future[GenericResponse] = ???

  override def addBreakpoint(id: Id): Future[GenericResponse] = ???

  override def removeBreakpoint(id: Id): Future[RemoveBreakpointResponse] = ???

  override def reset(): Future[OkOrUnhandledResponse] = ???

  override def pause: Future[PauseResponse] = ???

  override def resume: Future[OkOrUnhandledResponse] = ???

  override def getSequenceComponent: Future[AkkaLocation] = ???

  override def isAvailable: Future[Boolean] = Future.successful(sequenceState == Idle)

  override def isOnline: Future[Boolean] = Future.successful(online)

  override def goOnline(): Future[GoOnlineResponse] = {
    if (!online) sequenceState = Idle
    online = true
    Future.successful(Ok)
  }

  override def goOffline(): Future[GoOfflineResponse] = {
    if (online) sequenceState = Offline
    online = false
    sequenceOpt = None
    Future.successful(Ok)
  }

  override def abortSequence(): Future[OkOrUnhandledResponse] = ???

  override def stop(): Future[OkOrUnhandledResponse] = ???

  override def diagnosticMode(startTime: UTCTime, hint: String): Future[DiagnosticModeResponse] = Future.successful(Ok)

  override def operationsMode(): Future[OperationsModeResponse] = Future.successful(Ok)

  override def submit(sequence: Sequence): Future[SubmitResponse] = loadSequence(sequence).flatMap(_ => startSequence())

  override def submitAndWait(sequence: Sequence)(implicit timeout: Timeout): Future[SubmitResponse] =
    submit(sequence).flatMap {
      case Started(runId) => queryFinal(runId)(timeout)
      case res            => Future.successful(res)
    }

  override def query(runId: Id): Future[SubmitResponse] =
    Future.successful(crm.getOrElse(runId, Invalid(runId, IdNotAvailableIssue(""))))

  override def queryFinal(runId: Id)(implicit timeout: Timeout): Future[SubmitResponse] = {
    future(delayDuration, ()).flatMap(_ => query(runId))
  }
}
