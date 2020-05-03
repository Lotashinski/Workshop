package com.grsu.workshop.device.scanner

import android.bluetooth.BluetoothAdapter
import com.grsu.workshop.device.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject


class ScannerDevice : IDevice {

    private val _isClose = BehaviorSubject.create<IDevice>()
    private val _btAdapter = BluetoothAdapter.getDefaultAdapter()
    private val _btDevices = BluetoothDevices(_btAdapter)


    override val isCloseable: Observable<IDevice> = _isClose

    override fun uiAdapter(): IUiAdapter {
        return UiAdapter()
    }


    private val _devicesObservable = BehaviorSubject.create<List<IDeviceBuilder>>().apply {
        onNext(
            _btDevices.devices
        )
    }


    val deviceObservable: Observable<List<IDeviceBuilder>> = _devicesObservable


    fun update() {
        _devicesObservable.onNext(_btDevices.devices)
    }

    override fun close() {

    }
}