package ftf.grsu.workshop.services

import ftf.grsu.workshop.device.IMeterBuilder
import ftf.grsu.workshop.device.Meter

class PlaceboBuilder: IMeterBuilder {
    override val address: String
        get() = "PLACEBO"
    override val title: String
        get() = "PLACEBO"
    override val meter: Meter
        get() = Meter(address, PlaceboTransmitter())
}