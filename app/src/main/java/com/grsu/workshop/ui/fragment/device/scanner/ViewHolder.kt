package com.grsu.workshop.ui.fragment.device.scanner

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grsu.workshop.R
import com.grsu.workshop.device.IDeviceBuilder

class ViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {

    private val _root = itemView
    private val _title = _root.findViewById<TextView>(R.id.builder_title)
    private val _progress = _root.findViewById<ProgressBar>(R.id.bar_progress)
    private val _address = _root.findViewById<TextView>(R.id.builder_address)


    fun setBuilder(builder: IDeviceBuilder, callback: (IDeviceBuilder) -> Unit) {
        _title.text = builder.title
        _address.text = builder.address

        _root.setOnClickListener {
            _progress.visibility = View.VISIBLE
            callback(builder)
        }
    }
}