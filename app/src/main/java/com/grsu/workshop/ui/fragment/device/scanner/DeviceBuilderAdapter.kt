package com.grsu.workshop.ui.fragment.device.scanner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grsu.workshop.R
import com.grsu.workshop.device.IDeviceBuilder

class DeviceBuilderAdapter(
    builders: List<IDeviceBuilder>,
    onSelect: (IDeviceBuilder) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    private val _builders = builders
    private val _onSelect = onSelect

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_builder, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return _builders.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setBuilder(_builders[position], _onSelect)
    }


}