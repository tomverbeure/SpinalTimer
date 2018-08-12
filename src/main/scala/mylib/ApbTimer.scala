
package mylib

import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba3.apb.{Apb3, Apb3SlaveFactory}
import spinal.lib.bus.amba3.apb._
import spinal.lib.misc.InterruptCtrl

case class ApbTimer(width : Int) extends Component{
  val io = new Bundle{
    val apb   = slave(Apb3(Apb3Config(addressWidth = 8, dataWidth = 32)))
    val ticks = in Bits(width bits)
  }

  val timerA = Timer(width = 32)
  val busCtrl = Apb3SlaveFactory(io.apb)

  val timerABridge = timerA.driveFrom(busCtrl,0x40)(
     ticks  = List(io.ticks.orR),
    // By looping the timer full to the clears, it allows you to create an autoreload mode.
    clears = List(timerA.io.full)
  )

  val interruptCtrl = InterruptCtrl(1)
  val interruptCtrlBridge = interruptCtrl.driveFrom(busCtrl,0x10)
  interruptCtrl.io.inputs(0) := timerA.io.full
}

object ApbTimerVerilog {
  def main(args: Array[String]) {
    SpinalVerilog(new ApbTimer(8))
  }
}
