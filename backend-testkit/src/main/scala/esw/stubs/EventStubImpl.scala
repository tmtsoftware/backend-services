package esw.stubs

import java.time.Instant

import akka.Done
import akka.actor.typed.{ActorSystem, SpawnProtocol}
import akka.stream.scaladsl.Source
import csw.params.core.models.Id
import csw.params.events.{Event, EventKey, EventName, ObserveEvent}
import csw.prefix.models.Subsystem.CSW
import csw.prefix.models.{Prefix, Subsystem}
import csw.time.core.models.UTCTime
import esw.gateway.api.EventApi
import esw.gateway.api.protocol.{EmptyEventKeys, EventServerUnavailable, InvalidMaxFrequency}
import esw.gateway.impl.SourceExtensions.RichSource
import esw.ocs.testkit.utils.BaseTestSuite
import msocket.api.Subscription

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong

class EventStubImpl(_actorSystem: ActorSystem[SpawnProtocol.Command]) extends EventApi with BaseTestSuite {

  lazy val validObserveEvent: Event = ObserveEvent(Prefix(CSW, "ncc.trombone"), EventName("offline"))

  implicit def actorSystem: ActorSystem[SpawnProtocol.Command] = _actorSystem

  override def publish(event: Event): Future[Done] = {
    if (event.eventId == "InvalidEvent") Future.failed(new EventServerUnavailable)
    Future.successful(Done)
  }

  override def get(eventKeys: Set[EventKey]): Future[Set[Event]] = {
    if (eventKeys.nonEmpty) {
      Future.successful(Set(validObserveEvent))
    }
    else Future.failed(new EmptyEventKeys)
  }

  override def subscribe(eventKeys: Set[EventKey], maxFrequency: Option[Int]): Source[Event, Subscription] = {
    val stream =
      if (eventKeys.nonEmpty) validResponseStream
      else if (maxFrequency.getOrElse(0) <= 0) Source.failed(new InvalidMaxFrequency)
      else Source.failed(new EmptyEventKeys)

    stream.withSubscription()
  }

  override def pSubscribe(subsystem: Subsystem, maxFrequency: Option[Int], pattern: String): Source[Event, Subscription] = {
    val stream =
      if (maxFrequency.getOrElse(0) <= 0) Source.failed(new InvalidMaxFrequency)
      else validResponseStream

    stream.withSubscription()
  }

  private def validResponseStream: Source[Event, () => Unit] = {
    val futureStream = future(2.seconds, Source(List(validObserveEvent)))
    Source
      .futureSource(futureStream)
      .mapMaterializedValue(_ => () => ())
  }
}
