package ftf.grsu.workshop.services

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ftf.grsu.workshop.device.ITransmitter

class PlaceboTransmitter : ITransmitter {
    private val _sigLevel = MutableLiveData<Long>(5)

    override val signalLevelLive: LiveData<Long>
        get() = _sigLevel

    override fun getValue(uid: Byte): Long {
        if (uid == 0.toByte())
            return 500

        val ret = (90_000L + uid * 1_000) / uid
        return ret
    }

    override fun close() {};
}