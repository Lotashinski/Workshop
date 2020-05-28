package com.grsu.workshop.device.meter

import android.util.Log
import com.grsu.workshop.core.ThreadFactory
import java.io.Closeable
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class MeterUpdater(meter: Meter) : Closeable {

    private companion object Constants {
        private const val PERIOD = 1000L
    }

    private val _executor = Executors.newScheduledThreadPool(2, ThreadFactory("meter_updater"))

    @Volatile
    private var _task: ScheduledFuture<*>? = null
    private val _meter = meter

    private fun update() {
        _meter.update()
    }

    fun startTransmit() {
//        _task = _executor.scheduleAtFixedRate({
//            update()
//        }, PERIOD / 10, PERIOD, TimeUnit.MILLISECONDS)
         _meter.updateObservable.subscribe{
            _executor.submit {
                it.update()
            }
        }
    }

    override fun close() {
        Log.d("updater", "call close")
        _executor.submit {
                _task?.cancel(false)
        }.get()
        _executor.shutdown()
    }
}