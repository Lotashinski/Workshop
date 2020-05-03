package com.grsu.workshop.ui.activity

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grsu.workshop.device.IDevice
import com.grsu.workshop.device.IDeviceBuilder
import com.grsu.workshop.service.ConnectService

class MainViewModel : ViewModel() {

    init {
        Log.d("vm", "create")
    }

    private val _isLoad = MutableLiveData<Boolean>(false)
    private val _message = MutableLiveData<Int>()
    private val _device = MutableLiveData<IDevice>()

    @Volatile
    private var _isBind = false
    private lateinit var _service: ConnectService

    val isLoad: LiveData<Boolean> = _isLoad
    val message: LiveData<Int> = _message
    val device: LiveData<IDevice> = _device
    val isBind: Boolean
        get() = _isBind


    fun bindService(service: ConnectService) {
        _isBind = true
        _service = service

        service.deviceObservable.subscribe {
            object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    Log.d("main_view_model", "device bind " + it.javaClass.name)
                    _device.value = it
                }
            }.sendEmptyMessage(0)
        }

        service.isLoadObservable.subscribe {
            object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    Log.d("main_view_model", "is load  $it")
                    _isLoad.value = it
                }
            }.sendEmptyMessage(0)
        }

        service.massageObservable.subscribe {
            object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    Log.d("main_view_model", "send message id  $it")
                    _message.value = it
                }
            }.sendEmptyMessage(0)
        }

        Log.d("main_view_model", "service connected")
    }

    fun disconnectDevice() {
        _service.unbindDevice()
    }

    fun connectDevice(builder: IDeviceBuilder) {
        _service.bindDevice(builder)
    }
}
