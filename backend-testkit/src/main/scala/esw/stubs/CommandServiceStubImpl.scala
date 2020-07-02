package esw.stubs

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import akka.stream.scaladsl.Source
import akka.util.Timeout
import csw.command.api.StateMatcher
import csw.command.api.scaladsl.CommandService
import csw.location.api.scaladsl.LocationService
import csw.params.commands.CommandResponse.{Accepted, Completed, Started}
import csw.params.commands.{CommandResponse, ControlCommand}
import csw.params.core.models.Id
import csw.params.core.states.{CurrentState, StateName}
import esw.ocs.testkit.utils.LocationUtils
import msocket.api.Subscription

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong

class CommandServiceStubImpl(val locationService: LocationService, _actorSystem: ActorSystem[SpawnProtocol.Command])
    extends CommandService
    with LocationUtils {

  override implicit def actorSystem: ActorSystem[SpawnProtocol.Command] = _actorSystem

  override def validate(controlCommand: ControlCommand): Future[CommandResponse.ValidateResponse] =
    Future.successful(Accepted(Id()))

  override def submit(controlCommand: ControlCommand): Future[CommandResponse.SubmitResponse] = Future.successful(Started(Id()))

  override def submitAndWait(controlCommand: ControlCommand)(implicit timeout: Timeout): Future[CommandResponse.SubmitResponse] =
    future(5.seconds, Completed(Id()))

  override def submitAllAndWait(submitCommands: List[ControlCommand])(implicit
      timeout: Timeout
  ): Future[List[CommandResponse.SubmitResponse]] = future(10.seconds, submitCommands.map(_ => Completed(Id())))

  override def oneway(controlCommand: ControlCommand): Future[CommandResponse.OnewayResponse] = Future.successful(Accepted(Id()))

  override def onewayAndMatch(
      controlCommand: ControlCommand,
      stateMatcher: StateMatcher
  ): Future[CommandResponse.MatchingResponse] = ???

  override def query(commandRunId: Id): Future[CommandResponse.SubmitResponse] = Future.successful(Started(Id()))

  override def queryFinal(commandRunId: Id)(implicit timeout: Timeout): Future[CommandResponse.SubmitResponse] =
    future(5.seconds, Completed(Id()))

  override def subscribeCurrentState(names: Set[StateName]): Source[CurrentState, Subscription] = ???

  override def subscribeCurrentState(callback: CurrentState => Unit): Subscription = ???

  override def subscribeCurrentState(names: Set[StateName], callback: CurrentState => Unit): Subscription = ???

}
