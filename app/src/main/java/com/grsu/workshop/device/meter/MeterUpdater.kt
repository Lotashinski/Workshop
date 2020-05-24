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
    private val _lock = ReentrantLock()

    private fun update() {
        try {
            _lock.lock()
            Log.d("updater", "update")
            _meter.update()
        } catch (e: Throwable) {
            Log.e("updater", "update", e);
        } finally {
            _lock.unlock()
        }
    }

    fun startTransmit() {
        _task = _executor.scheduleAtFixedRate({
            update()
        }, PERIOD / 10, PERIOD, TimeUnit.MILLISECONDS)
    }

    override fun close() {
        _executor.submit {
            try {
                _lock.lock()
                _task?.cancel(false)
            } finally {
                _lock.unlock()
            }
        }.get()
        _executor.shutdown()
    }
}