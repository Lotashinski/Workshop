package com.grsu.workshop.device.meter

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

internal class BmpSource(override val uid: Byte, transmitter: ITransmitter): IBmpSource {

    private val _transmitter = transmitter

    private val _isActiveObservable = BehaviorSubject.create<Boolean>()

    @Volatile
    private var _value: Long = 0L;

    @Volatile
    private var _isActive: Boolean = true

    val isActiveObservable: Observable<Boolean> = _isActiveObservable

    override val pressure: Long
        get() = (_value and 0x00000000ffffffff)

    override var isActive: Boolean
        get() = _isActive
        set(value) {
            _isActive = value
            _isActiveObservable.onNext(value)
        }

    @ExperimentalUnsignedTypes
    override fun update() {
        _value = _transmitter.getPackage(uid)
        Log.d("bmp_source_$uid", "update value = $_value")
    }

    override fun close() {
        _isActiveObservable.onComplete()
    }
}