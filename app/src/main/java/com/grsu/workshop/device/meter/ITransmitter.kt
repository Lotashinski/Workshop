package com.grsu.workshop.device.meter

import java.io.Closeable

interface ITransmitter: Closeable{

    fun getPackage(uid: Byte): Long

}