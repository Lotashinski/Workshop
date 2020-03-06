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
import java.util.concurrent.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class BtRequestService : Service() {

    private companion object Constants {
        private const val PERIOD = 500L
    }

    internal inner class Binder : android.os.Binder() {
        val service: BtRequestService
            get() = this@BtRequestService
    }

    @SuppressLint("HandlerLeak")
    private inner class BuildersHandler(
        private val _builders: List<IMeterBuilder>
    ) : android.os.Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            Log.d("set list", "1")
            _meterBuilders.value = _builders
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
    private inner class ConnectHandler(
        private val _applicant: Meter
    ) : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            _meter.value = _applicant
            _isConnect.value = true
        }
    }

    @SuppressLint("HandlerLeak")
    private inner class ConnectValueHandler(private val value: Boolean) :
        Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            _isConnect.value = value
        }
    }

//    private val _bluetooth: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val _binder = Binder()

    private val _meterBuilders = MutableLiveData<List<IMeterBuilder>>(listOf())
    private val _meter = MutableLiveData<Meter>()
    private val _isConnect = MutableLiveData<Boolean>()
    private val _onLoad = MutableLiveData<Boolean>(false)
    private val _lock: Lock = ReentrantLock()

    private val _executor = Executors
        .newScheduledThreadPool(
            2,
            ThreadFactory("bt.request", Thread.UncaughtExceptionHandler { t, e ->

            })
        )

    val meterBuilders: LiveData<List<IMeterBuilder>> = _meterBuilders

    val currentMeter: LiveData<Meter> = _meter
    val isConnect: LiveData<Boolean> = _isConnect
    val onLoad: LiveData<Boolean> = _onLoad

    override fun onBind(intent: Intent): IBinder {
        Log.d("bt.request", "call onBind")
        return _binder
    }

    fun updateBounded() {
        val c = listOf(PlaceboBuilder())
//        Log.d("bt.request", "update bounded")
//        val c = _bluetooth.bondedDevices
//            .map { d -> MeterConnector(d, PERIOD, _executor) }
//            .toList()
        BuildersHandler(c).sendEmptyMessage(0)
    }

    private var _scheduledThread: ScheduledFuture<*>? = null

    fun connect(meterBuilder: IMeterBuilder) {
        disconnect()

        _executor.schedule({
            try {
                _lock.lock()
                OnLoadHandler(true).sendEmptyMessage(0)
                Log.d("bt.request.service", "connect to " + meterBuilder.address)
                val status = _isConnect.value ?: false

                if (status)
                    throw DeviceIsConnectException()

                val applicant = meterBuilder.meter
                ConnectHandler(applicant)
                    .sendEmptyMessage(0)

                _scheduledThread = _executor
                    .scheduleWithFixedDelay(
                        MeterUpdateRunnable(applicant),
                        0,
                        PERIOD,
                        TimeUnit.MILLISECONDS
                    )
            }
            // TODO invalid device exception
            catch (e: Exception) {
                Log.e("bt.request.exception", "exception on connect", e)
                disconnect()
            } finally {
                _lock.unlock()
                OnLoadHandler(false).sendEmptyMessage(0)
            }
        }, 0, TimeUnit.MILLISECONDS)
    }

    fun disconnect() {
        _lock.lock()
        ConnectValueHandler(false).sendEmptyMessage(0)
        _scheduledThread?.cancel(false)
        _meter.value?.close()
        _lock.unlock()
    }

    override fun onCreate() {
        Log.d("bt.request.service", "call create")
        super.onCreate()
    }

    override fun onDestroy() {
        disconnect()
        Log.d("bt.request.service", "call destroy")
        _executor.shutdownNow()
        super.onDestroy()
    }
}