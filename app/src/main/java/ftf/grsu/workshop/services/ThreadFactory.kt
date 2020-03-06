package ftf.grsu.workshop.services

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong

class ThreadFactory(
    private val _prefix: String,
    private val _exceptionHandler: Thread.UncaughtExceptionHandler
) : ThreadFactory {
    private companion object Counter{
        private val COUNTER = AtomicLong(0)
    }

    override fun newThread(r: Runnable): Thread = Thread(r).apply {
        name = _prefix + " " + COUNTER.incrementAndGet()
        uncaughtExceptionHandler = _exceptionHandler
    }
}