package com.grsu.workshop.device.meter

import com.grsu.workshop.device.ITransmitter

internal class BmpSource(override val uid: Byte, transmitter: ITransmitter
): IBmpSource {

    private val _transmitter = transmitter
    @Volatile
    private var _value: Long = 0L;

    @Volatile
    private var _isActive: Boolean = true

    override val pressure: Long
        get() = (_value and 0x00000000ffffffff)

    @ExperimentalUnsignedTypes
    override val temperature: Long
        get() = (_value and 0x7fffffff00000000) shr 32

    override var isActive: Boolean
        get() = _isActive
        set(value) {
            _isActive = value
        }

    @ExperimentalUnsignedTypes
    override fun update() {
        _value = _transmitter.getPackage(uid)
    }

    override fun close() {
    }
}