package ftf.grsu.workshop.device

import androidx.lifecycle.LiveData
import java.io.Closeable

interface IMeter : Closeable {

    val address: String

    val power: LiveData<Long>

    val updateCounter: LiveData<Long>

    var sources: List<Source>

    val title: String
}