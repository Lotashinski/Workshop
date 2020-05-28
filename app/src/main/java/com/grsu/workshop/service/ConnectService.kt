package com.grsu.workshop.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.grsu.workshop.R
import com.grsu.workshop.core.Scheduler
import com.grsu.workshop.device.DeviceConnectException
import com.grsu.workshop.device.IDevice
import com.grsu.workshop.device.IDeviceBuilder
import com.grsu.workshop.device.scanner.ScannerDevice
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock


class ConnectService : Service() {

    inner class ConnectServiceBinder : Binder() {
        val service
            get() = this@ConnectService
    }

    private val _lock = ReentrantLock()
    private val _executor = Executors.newScheduledThreadPool(3, com.grsu.workshop.core.ThreadFactory("service_thread") )
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

    private fun connect(builder: IDeviceBuilder){
        try {
            builder.connect { d ->
                Log.d("service", "start connect")
                _deviceObservable.onNext(d)
                _isLoadObservable.onNext(false)
                _messageObservable.onNext(R.string.message_device_connect)
                d.isCloseable.subscribe {
                    Log.d("service", "observe close")
                    _deviceObservable.onNext(ScannerDevice())
                    _messageObservable.onNext(R.string.message_device_disconnect)
                }
                d.isCloseable.subscribe{
                    Log.d("service", "call close in device")
                }
            }
        } catch (t: DeviceConnectException) {
            Log.e("service", "e", t)
            _messageObservable.onNext(R.string.message_device_connect_exception)
            _isLoadObservable.onNext(false)
            _deviceObservable.value.close()
            _deviceObservable.onNext(ScannerDevice())
        }
    }

    fun bindDevice(builder: IDeviceBuilder) {
        Log.d("service", "connect to device")
        _isLoadObservable.onNext(true)

        _executor.submit {
            if (!_lock.isLocked)
                try {
                    _lock.lock()
                    _executor.submit {
                        connect(builder)
                    }.get(15, TimeUnit.SECONDS)
                } catch (t: Throwable) {
                    Log.e("service", "e", t)
                    _isLoadObservable.onNext(false)
                    _deviceObservable.onNext(ScannerDevice())
                } finally {
                    _lock.unlock()
                }
        }

    }

    fun unbindDevice() {
        _deviceObservable.value.close()
        _deviceObservable.onNext(ScannerDevice())
        _messageObservable.onNext(R.string.message_device_disconnect)
    }

    override fun onDestroy() {
        _executor.shutdownNow()
        _deviceObservable.value.close()
        super.onDestroy()
    }
}
