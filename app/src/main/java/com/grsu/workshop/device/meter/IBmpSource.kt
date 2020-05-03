package com.grsu.workshop.device.meter


interface IBmpSource : ISource {
    val uid: Byte
    val pressure: Long
    var isActive: Boolean
}