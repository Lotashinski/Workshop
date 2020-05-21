package com.grsu.workshop.device

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
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
    private val _executor = Executors.newSingleThreadExecutor()

    private val _socket: BluetoothSocket
    private val _inputStream: InputStream
    private val _outputStream: OutputStream

    init {
        val uids = btDevice.uuids ?: throw UnsupportedDeviceException()
        val uid = uids.first()
        _socket = btDevice.createRfcommSocketToServiceRecord(uid.uuid)

        try {
            if (!_socket.isConnected) {
                _socket.connect()
            }
        }catch (io: IOException){
            Log.e("bt_transmitter", "connect exception", io)
        }

        _inputStream = _socket.inputStream
        _outputStream = _socket.outputStream
        _inputStream.read(ByteArray(8))
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