package ftf.grsu.workshop.services

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ftf.grsu.workshop.device.Meter
import ftf.grsu.workshop.device.IMeterBuilder
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class BtRequestService : Service() {

    internal inner class Binder : android.os.Binder() {
        val service: BtRequestService
            get() = this@BtRequestService
    }

    private inner class UpdateBoundedTask : TimerTask() {

        private lateinit var _connectors: CopyOnWriteArrayList<IMeterBuilder>

        override fun run() {
            val c = _bluetooth.bondedDevices
                .map { d -> MeterConnector(d) }
                .toList()

            _connectors = CopyOnWriteArrayList(c)
            BuildersHandler().sendEmptyMessage(0)
        }

        @SuppressLint("HandlerLeak")
        private inner class BuildersHandler : android.os.Handler(Looper.getMainLooper()) {

            override fun handleMessage(msg: Message) {
                _meterBuilders.value = _connectors
            }
        }
    }

    private inner class UpdateCurrentTask : TimerTask() {
        override fun run() {
            _lock.lock()
            Log.d("bt.request.timer", "call update()")
            _meter.value?.run {
                try {
                    this.update()
                } catch (e: Exception) {
                    Log.e("call.update", "call after close", e)
                }

            }
            _lock.unlock()
        }

        override fun cancel(): Boolean {
            _meter.value?.close()
            return super.cancel()
        }
    }

    @SuppressLint("HandlerLeak")
    private inner class OnLoadHandler(private val onLoad: Boolean) :
        Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            _onLoad.value = onLoad
        }
    }

    @SuppressLint("HandlerLeak")
    private inner class ConnectHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            _lock.lock()
            _meter.value = _applicant
            _lock.unlock()
            _current = _taskUpdateCurrent
            _timer.schedule(_current, 0L, PERIOD)
            _isConnect.value = true
        }
    }

    companion object Constants {
        private const val PERIOD = 300L
    }

    private val _meterBuilders = MutableLiveData<List<IMeterBuilder>>(listOf())
    private val _bluetooth: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val _binder = Binder()
    private val _meter = MutableLiveData<Meter>()
    private val _isConnect = MutableLiveData<Boolean>()
    private val _timer = Timer()
    private val _onLoad = MutableLiveData<Boolean>(false)
    private val _lock: Lock = ReentrantLock()

    private lateinit var _applicant: Meter

    private val _taskUpdateCurrent
        get() = UpdateCurrentTask()

    private val _taskUpdateBounded
        get() = UpdateBoundedTask()

    val meterBuilders: LiveData<List<IMeterBuilder>> = _meterBuilders
    val currentMeter: LiveData<Meter> = _meter
    val isConnect: LiveData<Boolean> = _isConnect
    val onLoad: LiveData<Boolean> = _onLoad

    private lateinit var _current: UpdateCurrentTask

    init {
        _timer.schedule(_taskUpdateBounded, 0L, 1000L)
    }

    override fun onBind(intent: Intent): IBinder = _binder

    fun connect(IMeterBuilder: IMeterBuilder) {
        if (isConnect.value == true)
            disconnect()

        _onLoad.value = true

        Thread(Runnable {
            try {
                OnLoadHandler(true).sendEmptyMessage(0)

                Log.d("bt.request.service", "connect to " + IMeterBuilder.address)
                val status = _isConnect.value ?: false

                if (status)
                    throw DeviceIsConnectException()

                _lock.lock()
                _applicant = IMeterBuilder.meter
                ConnectHandler()
                    .sendEmptyMessage(0)

            } catch (e: Exception) {
                Log.e("bt.request.exception", "exception on connect", e)
                disconnect()
            } finally {
                _lock.unlock()
                OnLoadHandler(false).sendEmptyMessage(0)
            }
        })
            .start()
    }

    fun disconnect() {
        _current.cancel()
        _isConnect.value = false
    }

    override fun onCreate() {
        Log.d("bt.request.service", "call create")
        super.onCreate()
    }

    override fun onDestroy() {
        disconnect()
        Log.d("bt.request.service", "call destroy")
        super.onDestroy()
    }
}