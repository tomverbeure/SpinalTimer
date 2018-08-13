
package mylib

import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba3.apb._

case class ApbTimer(width : Int) extends Component{
  val io = new Bundle{
    val apb       = slave(Apb3(Apb3Config(addressWidth = 8, dataWidth = 32)))
    val tick      = in Bool
    val interrupt = out Bool
  }

  val timer = Timer(width = 32)
  val busCtrl = Apb3SlaveFactory(io.apb)

  val timerBridge = timer.driveFrom(busCtrl,0x40)(
    // The 'True' allows for a mode where the timer increments each cycle without the need for activity on io.tick
    ticks  = Seq(True, io.tick),

    // By looping the timer full to the clears, it allows you to create an autoreload mode.
    clears = List(timer.io.full)
  )

  io.interrupt := timer.io.full
}

object ApbTimerVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new ApbTimer(8))
  }
}
