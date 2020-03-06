package ftf.grsu.workshop.services

import ftf.grsu.workshop.device.Meter

class MeterUpdateRunnable(
    private val _meter: Meter
): Runnable {

    override fun run() {
        _meter.update()
    }
}