package ftf.grsu.workshop.device

import androidx.lifecycle.LiveData

interface ISource {

    val title: String

    val isActive: LiveData<Boolean>

    val value: LiveData<Long>

    var active: Boolean

    fun update()
}