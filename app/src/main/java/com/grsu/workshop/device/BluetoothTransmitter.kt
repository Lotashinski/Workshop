package com.grsu.workshop.device

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.grsu.workshop.device.meter.ITransmitter
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.locks.ReentrantLock

class BluetoothTransmitter(private val _btDevice: BluetoothDevice) :
    ITransmitter {

    companion object{
        private val TIMEOUT = 135L
    }

    private val _lock = ReentrantLock()
    private val _executor = Executors.newSingleThreadExecutor()

    private val _socket: BluetoothSocket
    private val _inputStream: InputStream
    private val _outputStream: OutputStream

    init {
        val uids = _btDevice.uuids ?: throw UnsupportedDeviceException()
        val uid = uids.first()
        _socket = _btDevice.createRfcommSocketToServiceRecord(uid.uuid)


        try {
            if (!_socket.isConnected) {
                _socket.connect()
            }
        }catch (io: IOException){
            Log.d("bt_transmitter", "connect exception", io)
        }


        _inputStream = _socket.inputStream
        _outputStream = _socket.outputStream
        _inputStream.read(ByteArray(8))
    }


    private fun doRequest(uid: Byte): Long{
        val buffer = ByteArray(16)
        var counter = 0
        var inputVal = 0L

        Log.d("transmitter", "available = " + _inputStream.available())
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
        Log.d("transmitter", "input [$inputVal]")
        return inputVal
    }

    override fun getPackage(uid: Byte): Long {
        try{
            _lock.lock()
            return _executor.submit<Long>{
                return@submit doRequest(uid)
            }.get(TIMEOUT, TimeUnit.MILLISECONDS)
        }catch (eTimeOut: TimeoutException){
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