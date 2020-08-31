package org.tmt

import caseapp.RemainingArgs
import csw.prefix.models.Subsystem
import esw.commons.cli.EswCommandApp
import esw.ocs.api.models.ObsMode
import esw.ocs.testkit.EswTestKit
import org.tmt.TSSequencerCommands._

object SequencerApp extends EswCommandApp[TSSequencerCommands] {
  private lazy val eswTestKit = new EswTestKit() {}

  override def run(options: TSSequencerCommands, remainingArgs: RemainingArgs): Unit =
    options match {
      case Start(subsystem: Subsystem, observingMode: String) =>
        eswTestKit.spawnSequencerInSimulation(subsystem, ObsMode(observingMode))
    }

  override def exit(code: Int): Nothing = {
    eswTestKit.shutdownAllSequencers()
    super.exit(code)
  }
}
