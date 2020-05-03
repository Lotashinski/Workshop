package com.grsu.workshop.device

import com.grsu.workshop.device.meter.ITransmitter

class PlaceboTransmitter : ITransmitter {

    override fun getPackage(uid: Byte): Long {
        return uid.toLong()
    }

    override fun close() {
    }
}