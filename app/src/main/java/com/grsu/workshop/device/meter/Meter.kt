package com.grsu.workshop.device.meter

import android.util.Log
import com.grsu.workshop.device.IDevice
import com.grsu.workshop.device.IUiAdapter
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class Meter(transmitter: ITransmitter) : IDevice {

    private companion object Constants {
        private const val REQUEST_TIMEOUT = 980L
    }

    private val _lock = ReentrantLock()
    private val _isClose = BehaviorSubject.create<IDevice>()

    override val isCloseable: Observable<IDevice> = _isClose

    private val _transmitter = transmitter
    private val _updateObservable = BehaviorSubject.create<Meter>().apply { onNext(this@Meter) }
    private val _meterUpdater = MeterUpdater(this)
    private val _executor = Executors.newSingleThreadExecutor { r ->
        Thread(r).apply {
            name = "meter thread "
        }
    }
    private val _bmps = listOf(
        BmpSource(1, _transmitter),
        BmpSource(2, _transmitter),
        BmpSource(3, _transmitter),
        BmpSource(4, _transmitter),
        BmpSource(5, _transmitter),
        BmpSource(6, _transmitter)
    )


    override fun uiAdapter(): IUiAdapter {
        return UiAdapter()
    }

    val updateObservable: Observable<Meter> = _updateObservable
    val powerSource:IPowerSource = PowerSource(_transmitter)
    val bmpSources: List<IBmpSource>
        get() = _bmps

    init {
        Log.d("meter", "init")
        _bmps.forEach { s -> s.isActiveObservable.subscribe { update() } }
        _meterUpdater.startTransmit()
    }

    fun update() {
        try {
            _lock.lock()
            _executor.submit {
                bmpSources
                    .filter { s -> s.isActive }
                    .forEach { s -> s.update() }
                powerSource.update()
                _updateObservable.onNext(this)
            }.get(REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
        } catch (t: Throwable) {
            Log.e("meter", "exception", t)
            _updateObservable.onError(t)
        }finally {
            _lock.unlock()
        }
    }

    override fun close() {
        try {
            _lock.lock()
            _executor.shutdown()
            _meterUpdater.close()
            bmpSources.forEach { s -> s.close() }
            powerSource.close()
            _updateObservable.onComplete()
            _isClose.onNext(this)
            _isClose.onComplete()
            _transmitter.close()
        }catch (io: IOException){
            Log.e("meter", "disconnect io exception", io)
        }finally {
            _lock.unlock()
        }
    }
}