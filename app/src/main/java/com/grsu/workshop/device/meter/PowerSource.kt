package com.grsu.workshop.device.meter

import com.grsu.workshop.core.Scheduler
import com.grsu.workshop.device.ITransmitter
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

internal class PowerSource(transmitter: ITransmitter): IPowerSource {

    private val _transmitter = transmitter
    @Volatile
    private var _value: Long = 0L;
    private val _isUpdateObservable = BehaviorSubject.create<PowerSource>().apply {
        subscribeOn(Scheduler("power_source_update"))
    }

    val isUpdateObservable: Observable<PowerSource> = _isUpdateObservable

    override val value: Long
        get() = (_value and 0x7fffffffffffffff) / 10
    override val isCharging: Boolean
        get() = (_value shr 31) == 1L

    override fun update() {
        _value = _transmitter.getPackage(0)
        _isUpdateObservable.onNext(this)
    }

    override fun close() {
        _isUpdateObservable.onComplete()
    }
}