package ftf.grsu.workshop.ui.device.select

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ftf.grsu.workshop.R
import ftf.grsu.workshop.device.IMeterBuilder

class ViewHolderMeterBuilders(
    private val view: View
) : RecyclerView.ViewHolder(view) {

    private val titleText: TextView = view.findViewById(R.id.item_title)
    private val addressTitle: TextView = view.findViewById(R.id.item_address)

    fun setMeter(builderI: IMeterBuilder, callback : (IMeterBuilder) -> Unit) {
        titleText.text = builderI.title
        addressTitle.text = builderI.address
        view.setOnClickListener { callback(builderI) }
    }
}