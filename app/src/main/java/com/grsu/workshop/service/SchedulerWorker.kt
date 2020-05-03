package com.grsu.workshop.service

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SchedulerWorker(val title: String) : Scheduler.Worker() {

    private val _executor = Executors.newScheduledThreadPool(4)

    override fun isDisposed(): Boolean {
        return _executor.isTerminated
    }

    override fun schedule(run: Runnable?, delay: Long, unit: TimeUnit?): Disposable {
        val future = _executor.schedule(run ?: Runnable {  }, delay, unit ?: TimeUnit.MILLISECONDS)

        return object : Disposable{
            override fun isDisposed(): Boolean {
                return future.isCancelled
            }

            override fun dispose() {
                future.cancel(false)
            }
        }
    }

    override fun dispose() {
        _executor.shutdown()
    }

}