package ftf.grsu.workshop.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ftf.grsu.workshop.device.Meter
import ftf.grsu.workshop.device.IMeterBuilder

class DeviceViewModel : ViewModel() {


    private val _isConnect = MutableLiveData<Boolean>(false)
    private val _builders = MutableLiveData<List<IMeterBuilder>>(listOf())
    private val _current = MutableLiveData<Meter>()

    val isConnect: LiveData<Boolean> = _isConnect
    val builders: LiveData<List<IMeterBuilder>> = _builders
    val current: LiveData<Meter> = _current

    var connect: (IMeterBuilder) -> Unit = {}
    var disconnect: () -> Unit = {}

    fun setIsConnect(stat: Boolean) {
        _isConnect.value = stat
    }

    fun setCurrent(meter: Meter) {
        _current.value = meter
    }

    fun setBuilders(builderIS: List<IMeterBuilder>) {
        _builders.value = builderIS
    }

}