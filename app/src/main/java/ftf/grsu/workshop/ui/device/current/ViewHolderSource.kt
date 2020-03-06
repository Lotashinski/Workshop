package ftf.grsu.workshop.ui.device.current

import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ftf.grsu.workshop.R
import ftf.grsu.workshop.device.ISource

class ViewHolderSource(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val sourceText: TextView = view.findViewById(R.id.text_channel_title)
    private val activitySwitch: Switch = view.findViewById(R.id.switch_activity)

    fun setSource(source: ISource) {
        sourceText.text = source.title
        activitySwitch.isChecked = source.active
        activitySwitch.setOnCheckedChangeListener { _, isChecked ->
            source.active = isChecked
        }
    }
}