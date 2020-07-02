package esw.stubs

import akka.util.Timeout
import csw.location.api.models.AkkaLocation
import csw.params.commands.CommandResponse.Started
import csw.params.commands.{CommandResponse, Sequence, SequenceCommand}
import csw.params.core.models.Id
import csw.time.core.models.UTCTime
import esw.ocs.api.SequencerApi
import esw.ocs.api.models.StepList
import esw.ocs.api.protocol._

import scala.concurrent.Future

class SequencerServiceStubImpl extends SequencerApi {
  override def loadSequence(sequence: Sequence): Future[OkOrUnhandledResponse] = Future.successful(Ok)

  override def startSequence(): Future[CommandResponse.SubmitResponse] = Future.successful(Started(Id()))

  override def getSequence: Future[Option[StepList]] = ???

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

  override def isAvailable: Future[Boolean] = Future.successful(true)

  override def isOnline: Future[Boolean] = ???

  override def goOnline(): Future[GoOnlineResponse] = ???

  override def goOffline(): Future[GoOfflineResponse] = ???

  override def abortSequence(): Future[OkOrUnhandledResponse] = ???

  override def stop(): Future[OkOrUnhandledResponse] = ???

  override def diagnosticMode(startTime: UTCTime, hint: String): Future[DiagnosticModeResponse] = ???

  override def operationsMode(): Future[OperationsModeResponse] = ???

  override def submit(sequence: Sequence): Future[CommandResponse.SubmitResponse] = ???

  override def submitAndWait(sequence: Sequence)(implicit timeout: Timeout): Future[CommandResponse.SubmitResponse] = ???

  override def query(runId: Id): Future[CommandResponse.SubmitResponse] = ???

  override def queryFinal(runId: Id)(implicit timeout: Timeout): Future[CommandResponse.SubmitResponse] = ???
}
