package com.grsu.workshop.device

import io.reactivex.rxjava3.core.Observable
import java.io.Closeable

interface IDevice: Closeable {

    val isCloseable: Observable<IDevice>

    fun uiAdapter(): IUiAdapter

}