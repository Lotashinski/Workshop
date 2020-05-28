package com.grsu.workshop.device

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.grsu.workshop.core.ThreadFactory
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.locks.ReentrantLock

class BluetoothTransmitter(btDevice: BluetoothDevice) :
    ITransmitter {

    companion object{
        private val TIMEOUT = 250L
    }

    private val _lock = ReentrantLock()
    private val _executor = Executors.newSingleThreadExecutor(ThreadFactory("bt_transmit"))

    private val _socket: BluetoothSocket
    private val _inputStream: InputStream
    private val _outputStream: OutputStream

    init {
        Log.d("transmitter", "start init")
        val uids = btDevice.uuids ?: throw UnsupportedDeviceException()
        val uid = uids.first()
        _socket = btDevice.createRfcommSocketToServiceRecord(uid.uuid)


        if (!_socket.isConnected) {
             Log.d("transmitter", "connecting")
             _socket.connect()
             Log.d("transmitter", "connected")
        }

        Log.d("transmitter", "open socket")
        _inputStream = _socket.inputStream
        _outputStream = _socket.outputStream
        Log.d("transmitter", "socket is open")
        Log.d("transmitter", "init close")
    }


    @ExperimentalUnsignedTypes
    private fun doRequest(uid: Byte): Long{
        val buffer = ByteArray(16)
        var counter = 0
        var inputVal = 0L
        for (i in 0 until _inputStream.available())
            _inputStream.read()

        _outputStream.write(ByteArray(1) { uid })

        while (counter < 8) {
            val bytesCount = _inputStream.read(buffer)
            for (i in 0 until bytesCount) {
                if (counter < 8) {
                    inputVal += buffer[i].toUByte().toLong() shl (8 * counter)
                }
                counter++
            }
        }
        return inputVal
    }

    @ExperimentalUnsignedTypes
    override fun getPackage(uid: Byte): Long {
        val c = System.currentTimeMillis();
        try{
            _lock.lock()

            val r = _executor.submit<Long>{
                return@submit doRequest(uid)
            }.get(TIMEOUT, TimeUnit.MILLISECONDS)
            Log.d("transmitter", "$uid time " + ( System.currentTimeMillis() - c))

            return r;
        }catch (eTimeOut: TimeoutException){
            Log.e("transmitter", "timeout", eTimeOut);
            throw RequestTimeoutException(eTimeOut)
        }finally {
            _lock.unlock()
        }

    }

    override fun close() {
        try {
            _executor.shutdownNow()
            _lock.lock()
            _socket.close()
        }finally {
            _lock.unlock()
        }
    }

}