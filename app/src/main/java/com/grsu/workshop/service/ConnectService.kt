package com.grsu.workshop.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.grsu.workshop.R
import com.grsu.workshop.device.IDevice
import com.grsu.workshop.device.IDeviceBuilder
import com.grsu.workshop.device.scanner.ScannerDevice
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.locks.ReentrantLock

class ConnectService : Service() {

    inner class ConnectServiceBinder : Binder() {
        val service
            get() = this@ConnectService
    }

    private val _lock = ReentrantLock()
    private val _executor = Executors.newSingleThreadScheduledExecutor()
    private val _isLoadObservable = BehaviorSubject.create<Boolean>().apply {
        subscribeOn(Scheduler("service_is_load_worker"))
        onNext(false)
    }
    private val _deviceObservable = BehaviorSubject.createDefault<IDevice>(ScannerDevice()).apply {
        subscribeOn(Scheduler("service_device_worker"))
    }
    private val _messageObservable = BehaviorSubject.create<Int>().apply {
        subscribeOn(Scheduler("service_message_worker"))
    }

    val isLoadObservable: Observable<Boolean> = _isLoadObservable
    val deviceObservable: Observable<IDevice> = _deviceObservable
    val massageObservable: Observable<Int> = _messageObservable


    override fun onBind(intent: Intent): IBinder = ConnectServiceBinder()


    fun bindDevice(builder: IDeviceBuilder) {
        Log.d("service", "connect to device")
        _isLoadObservable.onNext(true)
        // todo error message for user
        try {
            _lock.lock()
            _executor.submit {
                try {
                    builder.connect { d ->
                        _deviceObservable.onNext(d)
                        _isLoadObservable.onNext(false)
                        _messageObservable.onNext(R.string.message_device_connect)
                    }
                } catch (t: Throwable) {
                    try {
                        _deviceObservable.value.close()
                    } catch (tr: Throwable) {
                        //ignore
                    }
                    _deviceObservable.onNext(ScannerDevice())
                }
            }.get(10, TimeUnit.SECONDS)
        } catch (t: Throwable) {
            // timeout or ioe
            _deviceObservable.onNext(ScannerDevice())
        }finally {
            _lock.unlock()
        }

    }

    fun unbindDevice() {
        _deviceObservable.value.close()
        _deviceObservable.onNext(ScannerDevice())
        _messageObservable.onNext(R.string.message_device_disconnect)
    }
}
