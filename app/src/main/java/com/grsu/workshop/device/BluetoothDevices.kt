package com.grsu.workshop.device

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.util.Log
import com.grsu.workshop.device.meter.Meter

class BluetoothDevices(private val _bluetooth: BluetoothAdapter) {

    class DeviceBuilder(private val _btDevice: BluetoothDevice) : IDeviceBuilder {

        override val title: String
            get() = _btDevice.name

        override val address: String
            get() = _btDevice.address

        override fun connect(callback: (IDevice) -> Unit) {
            try {
                Log.d("builder", "create call")
                val transmitter = BluetoothTransmitter(_btDevice)
                Log.d("builder", "transmitter build")
                val meter = Meter(transmitter)
                callback(meter)
            }catch (t: Throwable){
                Log.e("device_builder", "build exception", t)
                throw DeviceConnectException(t)
            }
        }
    }

    val devices: List<IDeviceBuilder>
        get() {
            return _bluetooth.bondedDevices
                .map { b -> DeviceBuilder(b) }
                .toList()
        }
}