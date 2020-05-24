package com.grsu.workshop.device.scanner

import android.bluetooth.BluetoothAdapter
import com.grsu.workshop.core.Scheduler
import com.grsu.workshop.device.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject


class ScannerDevice : IDevice {

    private val _isClose = BehaviorSubject.create<IDevice>().apply {
        subscribeOn(Scheduler("scanner_close"))
    }
    private val _btAdapter = BluetoothAdapter.getDefaultAdapter()
    private val _btDevices = BluetoothDevices(_btAdapter)


    override val isCloseable: Observable<IDevice> = _isClose

    override fun uiAdapter(): IUiAdapter {
        return UiAdapter()
    }


    private val _devicesObservable = BehaviorSubject.create<List<IDeviceBuilder>>().apply {
        subscribeOn(Scheduler("scanner_devices_update"))
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