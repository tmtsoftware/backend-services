package esw.stubs

import java.net.URI
import java.nio.file.{Path, Paths}
import java.time.Instant

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import csw.config.api.ConfigData
import csw.config.api.scaladsl.ConfigService
import csw.config.models._
import esw.ocs.testkit.utils.BaseTestSuite

import scala.concurrent.Future

class ConfigStubImpl(_actorSystem: ActorSystem[SpawnProtocol.Command]) extends ConfigService with BaseTestSuite {

  implicit def actorSystem: ActorSystem[SpawnProtocol.Command] = _actorSystem

  private lazy val configId   = ConfigId("Config1")
  private lazy val path       = Paths.get(new URI("file:///some-path"))
  private lazy val configData = ConfigData.fromString("this is config file")
  private lazy val author     = "some_author"
  private lazy val comment    = "some comment"

  override def create(path: Path, configData: ConfigData, annex: Boolean, comment: String): Future[ConfigId] =
    Future.successful(configId)

  override def update(path: Path, configData: ConfigData, comment: String): Future[ConfigId] = Future.successful(configId)

  override def getById(path: Path, id: ConfigId): Future[Option[ConfigData]] = {
    if (id == configId) Future.successful(Some(configData))
    else Future.successful(None)
  }

  override def getLatest(path: Path): Future[Option[ConfigData]] = {
    if (path.toUri.getPath == "/some-path") Future.successful(Some(configData))
    else Future.successful(None)
  }

  override def getByTime(path: Path, time: Instant): Future[Option[ConfigData]] = {
    if (path.toUri.getPath == "/some-path") Future.successful(Some(configData))
    else Future.successful(None)
  }

  override def delete(path: Path, comment: String): Future[Unit] = Future.successful()

  override def list(fileType: Option[FileType], pattern: Option[String]): Future[List[ConfigFileInfo]] = {
    Future.successful(List(ConfigFileInfo(path, configId, author, comment)))
  }

  override def history(path: Path, from: Instant, to: Instant, maxResults: Int): Future[List[ConfigFileRevision]] = {
    Future.successful(List(ConfigFileRevision(configId, author, comment, Instant.now)))
  }

  override def historyActive(path: Path, from: Instant, to: Instant, maxResults: Int): Future[List[ConfigFileRevision]] = {
    Future.successful(List(ConfigFileRevision(configId, author, comment, Instant.now)))
  }

  override def setActiveVersion(path: Path, id: ConfigId, comment: String): Future[Unit] = Future.successful()

  override def resetActiveVersion(path: Path, comment: String): Future[Unit] = Future.successful()

  override def getActiveVersion(path: Path): Future[Option[ConfigId]] = {
    if (path.toUri.getPath == "/some-path") Future.successful(Some(configId))
    else Future.successful(None)
  }

  override def getActiveByTime(path: Path, time: Instant): Future[Option[ConfigData]] = {
    if (path.toUri.getPath == "/some-path") Future.successful(Some(configData))
    else Future.successful(None)
  }

  override def getMetadata: Future[ConfigMetadata] = {
    Future.successful(ConfigMetadata("/repo-path", "/annex-path", "10 MiB", "20 MiB"))
  }

  override def exists(path: Path, id: Option[ConfigId]): Future[Boolean] = {
    Future.successful(true)
  }

  override def getActive(path: Path): Future[Option[ConfigData]] = {
    if (path.toUri.getPath == "/some-path") Future.successful(Some(configData))
    else Future.successful(None)
  }
}
