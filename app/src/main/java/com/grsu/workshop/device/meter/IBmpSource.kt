package com.grsu.workshop.device.meter


interface IBmpSource : ISource {
    val uid: Byte
    val pressure: Long
    val temperature: Long
    var isActive: Boolean
}