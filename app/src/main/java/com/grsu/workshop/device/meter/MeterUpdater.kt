package com.grsu.workshop.device.meter

import android.util.Log
import java.io.Closeable
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock

class MeterUpdater(meter: Meter) : Closeable {

    private companion object Constants {
        @Volatile
        private var COUNTER = 0L;

        private const val PERIOD = 1000L

    }

    private val _executor = Executors.newScheduledThreadPool(4) { r ->
        Thread(r).apply {
            name = "worker thread " + COUNTER++
        }
    }

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
        }, 0, PERIOD, TimeUnit.MILLISECONDS)
    }

    override fun close() {
        _executor.execute {
            try {
                _lock.lock()
                _task?.cancel(false)
            } finally {
                _lock.unlock()
            }
        }
    }
}