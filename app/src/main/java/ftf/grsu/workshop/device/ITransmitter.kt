package ftf.grsu.workshop.device

import androidx.lifecycle.LiveData
import java.io.Closeable

interface ITransmitter : Closeable {

    val signalLevelLive: LiveData<Long>

    fun getValue(uid: Byte): Long
}