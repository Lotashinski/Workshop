package com.grsu.workshop.device

import java.lang.RuntimeException

class DeviceConnectException: RuntimeException{
    constructor(): super()
    constructor(e: Throwable) : super(e)
}