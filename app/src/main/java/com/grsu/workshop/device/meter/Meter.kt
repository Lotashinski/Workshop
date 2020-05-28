package com.grsu.workshop.device.meter

import android.util.Log
import com.grsu.workshop.core.Scheduler
import com.grsu.workshop.core.ThreadFactory
import com.grsu.workshop.device.IDevice
import com.grsu.workshop.device.ITransmitter
import com.grsu.workshop.device.IUiAdapter
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class Meter(transmitter: ITransmitter) : IDevice {

    private companion object Constants {
        private const val REQUEST_TIMEOUT = 990L
    }

    private val _lock = ReentrantLock()

    private val _isClose = PublishSubject.create<IDevice>()

    override val isCloseable: Observable<IDevice> = _isClose

    private val _transmitter = transmitter
    private val _updateObservable = BehaviorSubject.create<Meter>().apply {
        subscribeOn(Scheduler("bmp_source_update"))
        onNext(this@Meter)
    }
    private val _meterUpdater = MeterUpdater(this)
    private val _executor = Executors.newSingleThreadExecutor(ThreadFactory("meter_update"))

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
        _meterUpdater.startTransmit()
    }

    fun update() {
        try {
            Log.d("meter", "call update")
            val cl = System.currentTimeMillis();
            Log.d("meter", "locked")
            _lock.lock()
            _executor.submit {
                bmpSources
                    .filter { s -> s.isActive }
                    .forEach { s -> s.update() }
                powerSource.update()
                _updateObservable.onNext(this)
            }.get(REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
            Log.d("meter", "time " + (System.currentTimeMillis() - cl))
        } catch (t: Throwable) {
            Log.e("meter", "exception", t)
            Log.d("meter", "unlock")
            _lock.unlock()
            close()
            Log.d("meter", "locked")
            _lock.lock()
        }finally {
            Log.d("meter", "unlock")
            _lock.unlock()
        }
    }

    override fun close() {
        Log.d("meter", "call close")
        try {
            Log.d("meter", "locked")
            _lock.lock()
            _executor.shutdown()
            _meterUpdater.close()
            bmpSources.forEach { s -> s.close() }
            powerSource.close()
            _updateObservable.onComplete()
            Log.d("meter", "send close signal")
            _isClose.onNext(this)
            _updateObservable.onComplete()
            _isClose.onComplete()
            _transmitter.close()
        }catch (io: IOException){
            Log.e("meter", "disconnect io exception", io)
        }finally {
            _lock.unlock()
        }
    }
}