package com.grsu.workshop.ui.fragment.device.meter

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grsu.workshop.R
import com.grsu.workshop.device.meter.IBmpSource
import com.grsu.workshop.device.meter.Meter
import com.grsu.workshop.device.meter.IPowerSource
import io.reactivex.rxjava3.functions.Consumer

class MeterViewModel : ViewModel() {

    companion object Constants {
        val COLORS = mapOf(
            "A" to R.color.colorA,
            "B" to R.color.colorB,
            "C" to R.color.colorC,
            "D" to R.color.colorD,
            "E" to R.color.colorE,
            "F" to R.color.colorF
        )
    }

    enum class Tab(val fragment: () -> Fragment) {
        ABSOLUTE({ MeterAbsoluteFragment() }),
        AVERAGE({ MeterAverageFragment() });
    }

    private val _meter = MutableLiveData<Meter>()
    private val _sources = MutableLiveData<List<IBmpSource>>()
    private val _powerSource = MutableLiveData<IPowerSource>()
    private val _tab = MutableLiveData<Tab>(Tab.ABSOLUTE)

    val meter: LiveData<Meter> = _meter
    val sources: LiveData<List<IBmpSource>> = _sources
    val powerSource: LiveData<IPowerSource> = _powerSource
    val tab: LiveData<Tab> = _tab
    val colors = COLORS

    fun bindMeter(meter: Meter) {
        _meter.value = meter
        meter.updateObservable.subscribe({ m ->
            object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    _sources.value = m.bmpSources.filter {
                        it.isActive
                    }
                    _powerSource.value = m.powerSource
                }
            }.sendEmptyMessage(0)
        }, {
            _meter.value?.close()
        })

    }

    fun selectTab(t: Tab) {
        _tab.value = t
    }

}