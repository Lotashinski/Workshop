package com.grsu.workshop.device

import java.io.Closeable

interface ITransmitter: Closeable{

    fun getPackage(uid: Byte): Long

}