package ftf.grsu.workshop.ui.chart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChartDataViewModel : ViewModel() {

    private val _isAdaptive = MutableLiveData<Boolean>(true)
    private val _minBar = MutableLiveData<Float>(0F)

    val minBar: LiveData<Float> = _minBar
    val isAdaptive: LiveData<Boolean> = _isAdaptive

    fun setMinBar(value: Float) {
        _minBar.value = value
    }

    fun setAdaptivity(value: Boolean) {
        _isAdaptive.value = value
    }
}