package com.grsu.workshop.ui.fragment.device.scanner

import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grsu.workshop.device.IDeviceBuilder
import com.grsu.workshop.device.scanner.ScannerDevice
import io.reactivex.rxjava3.disposables.Disposable

class ScannerViewModel : ViewModel() {

    private val _isLoad = MutableLiveData(false)
    private val _builders = MutableLiveData<List<IDeviceBuilder>>()
    private var _consumerDispose: Disposable? = null
    private var _scanner: ScannerDevice? = null

    val builders: LiveData<List<IDeviceBuilder>> = _builders
    val isLoad: LiveData<Boolean> = _isLoad


    fun updateAvailable() {
        _isLoad.value = true
        _scanner?.update()
    }

    fun bindDevice(scanner: ScannerDevice) {
        _scanner = scanner
        _consumerDispose?.dispose()
        _consumerDispose = scanner.deviceObservable.subscribe {
            object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    _builders.value = it
                    _isLoad.value = false
                }
            }.sendEmptyMessage(0)
        }
    }

}