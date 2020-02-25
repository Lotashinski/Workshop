package ftf.grsu.workshop.services

import android.bluetooth.BluetoothSocket
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ftf.grsu.workshop.device.ITransmitter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.locks.ReentrantLock

class Transmitter(
    private val bluetoothSocket: BluetoothSocket
) : ITransmitter {

    companion object Constants {
        private val LOCK = ReentrantLock()
    }

    private val _level = MutableLiveData<Long>(50L)
    private val _executor = Executors.newSingleThreadExecutor()

    init {
        Log.d("transmitter", "connect")

        LOCK.lock()
        if (!bluetoothSocket.isConnected)
            bluetoothSocket.connect()

        bluetoothSocket.inputStream.read(ByteArray(8))

        LOCK.unlock()
    }

    override val signalLevelLive: LiveData<Long>
        get() = _level


    @ExperimentalUnsignedTypes
    override fun getValue(uid: Byte): Long {
        try {
            return _executor.submit<Long> {
                LOCK.lock()
                Log.d("transmitter", "get value $uid")
                val inputStream = bluetoothSocket.inputStream
                val outputStream = bluetoothSocket.outputStream
                val buffer = ByteArray(16)
                var counter = 0
                var inputVal = 0L
                outputStream.write(ByteArray(1) { uid })
                while (counter < 8) {
                    val bytesCount = inputStream.read(buffer)
                    for (i in 0 until bytesCount) {
                        if (counter < 8)
                            inputVal += buffer[i].toUByte().toLong() shl (8 * counter)
                        counter++
                    }
                }
                Log.d("transmitter", "$uid result $inputVal")
                LOCK.unlock()
                return@submit inputVal
            }
                .get(500, TimeUnit.MILLISECONDS)
        } catch (eTimeOut: TimeoutException) {
            Log.e("transmitter", "timeout", eTimeOut)
            LOCK.unlock()
            throw eTimeOut
        }
    }

    override fun close() {
        LOCK.lock()
        Log.d("transmitter", "close")
        _executor.shutdownNow()
        bluetoothSocket.inputStream.close()
        bluetoothSocket.outputStream.close()
        bluetoothSocket.close()

        LOCK.unlock()
    }
}