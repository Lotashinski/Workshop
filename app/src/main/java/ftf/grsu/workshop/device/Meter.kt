package ftf.grsu.workshop.device

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.atomic.AtomicLong


class Meter(
    override val address: String,
    private val transmitter: ITransmitter
) : IMeter {
    private val _power = Source("Power", true, transmitter, 0)
    private val _updateCounter = MutableLiveData<Long>(0L)

    override val power = _power.value
    override val updateCounter: LiveData<Long> = _updateCounter
    override var sources: List<Source> = listOf(
        Source("A", true, transmitter, 1),
        Source("B", true, transmitter, 2),
        Source("C", true, transmitter, 3),
        Source("D", true, transmitter, 4),
        Source("E", true, transmitter, 5),
        Source("F", true, transmitter, 6)
    )

    override val title = "Pressure meter(x6)"
    private val _counter = AtomicLong(0L)

    fun update() {
        Log.d("meter $address", "call update")

        _power.update()

        val aSources = sources
            .filter { source -> source.active }

        if (aSources.count() == 0)
            return

        aSources.forEach { source -> source.update() }
        CurrentHandler().sendEmptyMessage(0)
    }

    @SuppressLint("HandlerLeak")
    inner class CurrentHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            _updateCounter.value = _counter.incrementAndGet()
        }
    }

    override fun close() {
        Log.d("Meter", "close")
        transmitter.close()
    }
}