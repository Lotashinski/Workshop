package com.grsu.workshop.device.meter

import java.io.Closeable

interface ISource: Closeable {

    fun update()

}