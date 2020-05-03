package com.grsu.workshop.ui.fragment.device.meter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grsu.workshop.R
import com.grsu.workshop.device.meter.IBmpSource

@ExperimentalUnsignedTypes
class ChannelAdapter(
    private val _sources: List<IBmpSource>,
    private val _mapTitle: List<String>
) : RecyclerView.Adapter<ChannelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_channel, parent, false)

        return ChannelViewHolder(view)
    }

    override fun getItemCount(): Int {
        return _sources.count()
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.setSource(_sources[position], _mapTitle[position])
    }
}