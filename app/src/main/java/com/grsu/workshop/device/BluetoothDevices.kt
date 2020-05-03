package com.grsu.workshop.device

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import com.grsu.workshop.device.meter.Meter

class BluetoothDevices(private val _bluetooth: BluetoothAdapter) {

    class DeviceBuilder(private val _btDevice: BluetoothDevice) : IDeviceBuilder {

        override val title: String
            get() = _btDevice.name

        override val address: String
            get() = _btDevice.address

        override fun connect(callback: (IDevice) -> Unit) {
            val _transmitter = BluetoothTransmitter(_btDevice)
            val meter = Meter(_transmitter)
            callback(meter)
        }
    }

    val devices: List<IDeviceBuilder>
        get() {
            return _bluetooth.bondedDevices
                .map { b -> DeviceBuilder(b) }
                .toList()
        }
}