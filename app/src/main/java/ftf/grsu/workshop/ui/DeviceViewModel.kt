package ftf.grsu.workshop.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ftf.grsu.workshop.R
import ftf.grsu.workshop.device.Meter
import ftf.grsu.workshop.device.IMeterBuilder

class DeviceViewModel : ViewModel() {

    private val _isConnect = MutableLiveData<Boolean>(false)
    private val _builders = MutableLiveData<List<IMeterBuilder>>(listOf())
    private val _current = MutableLiveData<Meter>()
    private val _loading = MutableLiveData<Boolean>(false)

    val isConnect: LiveData<Boolean> = _isConnect
    val builders: LiveData<List<IMeterBuilder>> = _builders
    val current: LiveData<Meter> = _current
    val loading: LiveData<Boolean> = _loading

    var connect: (IMeterBuilder) -> Unit = {}
    var disconnect: () -> Unit = {}
    var updateBuilders: () -> Unit = {}

    val colors = mapOf(
        "A" to R.color.colorA,
        "B" to R.color.colorB,
        "C" to R.color.colorC,
        "D" to R.color.colorD,
        "E" to R.color.colorE,
        "F" to R.color.colorF
    )

    fun setIsLoading(loading: Boolean){
        _loading.value = loading
    }

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