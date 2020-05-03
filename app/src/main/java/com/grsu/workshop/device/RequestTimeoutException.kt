package com.grsu.workshop.device

import java.lang.RuntimeException

class RequestTimeoutException: RuntimeException {
    constructor(): super()
    constructor(t: Throwable): super(t)
}