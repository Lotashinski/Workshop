package com.grsu.workshop.device.meter

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

internal class PowerSource(transmitter: ITransmitter): IPowerSource {

    private val _transmitter = transmitter
    @Volatile
    private var _value: Long = 0L;
    private val _isUpdateObservable = BehaviorSubject.create<PowerSource>()

    val isUpdateObservable: Observable<PowerSource> = _isUpdateObservable

    override val value: Long
        get() = (_value and 0x7fffffffffffffff) / 10
    override val isCharging: Boolean
        get() = (_value shr 31) == 1L

    override fun update() {
        _value = _transmitter.getPackage(0)
        Log.d("power_source", "update value = $_value")
        _isUpdateObservable.onNext(this)
    }

    override fun close() {
        _isUpdateObservable.onComplete()
    }
}