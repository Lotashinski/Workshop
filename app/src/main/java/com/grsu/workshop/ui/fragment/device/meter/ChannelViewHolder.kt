package com.grsu.workshop.ui.fragment.device.meter

import android.annotation.SuppressLint
import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grsu.workshop.R
import com.grsu.workshop.device.meter.IBmpSource

class ChannelViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView){

    private val _root = itemView
    private val _title = _root.findViewById<TextView>(R.id.channel_title)
    private val _switch = _root.findViewById<Switch>(R.id.switch_active)

    @SuppressLint("SetTextI18n")
    @ExperimentalUnsignedTypes
    fun setSource(source: IBmpSource, title: String) {
        _title.text = title

        _switch.isChecked = source.isActive
        _switch.setOnCheckedChangeListener { _, isChecked ->
            source.isActive = isChecked
        }
    }


}