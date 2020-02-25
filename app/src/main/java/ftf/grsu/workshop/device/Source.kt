package ftf.grsu.workshop.device

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.atomic.AtomicLong


class Source(
    override val title: String,

    isActive: Boolean,

    private val transmitter: ITransmitter,
    private val uid: Byte
) : ISource {

    private val _atomicLong = AtomicLong(0L)
    private var _isActive = MutableLiveData<Boolean>(isActive)
    private var _value = MutableLiveData<Long>(0L)

    override val isActive: LiveData<Boolean> = _isActive
    override val value: LiveData<Long> = _value
    override var active: Boolean
        get() = _isActive.value!!
        set(v) {
            _isActive.value = v
        }

    override fun update() {
        _atomicLong.set(transmitter.getValue(uid))
        CurrentHandler()
            .sendEmptyMessage(0)
    }

    @SuppressLint("HandlerLeak")
    inner class CurrentHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            _value.value = _atomicLong.get()
        }
    }
}