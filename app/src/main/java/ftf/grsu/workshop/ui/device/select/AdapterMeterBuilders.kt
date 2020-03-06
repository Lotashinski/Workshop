package ftf.grsu.workshop.ui.device.select

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ftf.grsu.workshop.R
import ftf.grsu.workshop.device.IMeterBuilder

class AdapterMeterBuilders(
    private var _builders: List<IMeterBuilder>,
    private val _callback: (IMeterBuilder) -> Unit
) : RecyclerView.Adapter<ViewHolderMeterBuilders>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMeterBuilders {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_device_builder_item, parent, false)

        return ViewHolderMeterBuilders(root)
    }

    override fun getItemCount(): Int = _builders.size

    override fun onBindViewHolder(holder: ViewHolderMeterBuilders, position: Int) =
        holder.setMeter(_builders[position], _callback)
}