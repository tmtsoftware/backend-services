package esw.stubs

import csw.admin.api.AdminService
import csw.location.api.models.ComponentId
import csw.logging.models.Level._
import csw.logging.models.{Level, LogMetadata}

import scala.concurrent.Future

class AdminStubImpl extends AdminService {
  override def getLogMetadata(componentId: ComponentId): Future[LogMetadata] =
    Future.successful(LogMetadata(INFO, DEBUG, INFO, ERROR))

  override def setLogLevel(componentId: ComponentId, level: Level): Future[Unit] = Future.unit
}
