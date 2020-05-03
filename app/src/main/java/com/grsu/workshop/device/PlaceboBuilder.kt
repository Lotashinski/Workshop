package com.grsu.workshop.device

import com.grsu.workshop.device.meter.Meter

class PlaceboBuilder : IDeviceBuilder {

    override val title: String
        get() = "PLACEBO"
    override val address: String
        get() = "PLACEBO"

    @ExperimentalUnsignedTypes
    override fun connect(callback: (IDevice) -> Unit) {
        Thread(Runnable {
            Thread.sleep(5000)
            callback(Meter(PlaceboTransmitter()))
        }).start()
    }
}