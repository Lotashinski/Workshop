package com.grsu.workshop.device.meter

interface IPowerSource : ISource {
    val value: Long
    val isCharging: Boolean
}