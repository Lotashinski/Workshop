package com.grsu.workshop.device

interface IDeviceBuilder {

    val title: String
    val address: String

    fun connect(callback: (IDevice) -> Unit)

}