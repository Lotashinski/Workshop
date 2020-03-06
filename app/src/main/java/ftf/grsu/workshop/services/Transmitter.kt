package ftf.grsu.workshop.services

import android.bluetooth.BluetoothSocket
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ftf.grsu.workshop.device.ITransmitter
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.locks.ReentrantLock

@ExperimentalUnsignedTypes
class Transmitter(
    private val _bluetoothSocket: BluetoothSocket,
    private val _timeout: Long,
    private val _executorService: ExecutorService
) : ITransmitter {

    companion object Constants {
        private val LOCK = ReentrantLock()
        private const val TIMEOUT_CONNECT: Long = 10000
    }

    private val _level = MutableLiveData<Long>(50L)

    init {
        try {
            LOCK.lock()
            _executorService.submit {
                Log.d("transmitter", "call connect")
                if (!_bluetoothSocket.isConnected)
                    _bluetoothSocket.connect()
                _bluetoothSocket.inputStream.read(ByteArray(8))
            }.get(TIMEOUT_CONNECT, TimeUnit.MILLISECONDS)
        } catch (e: TimeoutException) {
            throw DeviceConnectTimeoutException()
        } finally {
            LOCK.unlock()
        }
    }

    override val signalLevelLive: LiveData<Long>
        get() = _level

    private fun call(uid: Byte, timeoutMilliseconds: Long): Long {
        Log.d("transmitter", "call value $uid")
        try {
            LOCK.lock()
            return _executorService.submit<Long> {
                Log.d("transmitter.schedule", "call $uid")
                val inputStream = _bluetoothSocket.inputStream
                val outputStream = _bluetoothSocket.outputStream
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
                Log.d("transmitter", "($uid) -> $inputVal")
                return@submit inputVal
            }
                .get(timeoutMilliseconds, TimeUnit.MILLISECONDS)
        } catch (eTimeOut: TimeoutException) {
            Log.e("transmitter", "timeout", eTimeOut)
            throw eTimeOut
        } finally {
            LOCK.unlock()
        }
    }

    override fun getValue(uid: Byte): Long = call(uid, _timeout)

    override fun close() {
        LOCK.lock()
        Log.d("transmitter", "close")
        _bluetoothSocket.inputStream.close()
        _bluetoothSocket.outputStream.close()
        _bluetoothSocket.close()
        LOCK.unlock()
    }
}