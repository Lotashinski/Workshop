package ftf.grsu.workshop.ui.device.current

import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import ftf.grsu.workshop.R
import ftf.grsu.workshop.device.Source

class ViewHolderSource(
    view: View
) : RecyclerView.ViewHolder(view) {

    private val sourceText: TextView = view.findViewById(R.id.text_channel_title)
    private val activitySwitch: Switch = view.findViewById(R.id.switch_activity)

    fun setSource(source: Source) {
        sourceText.text = source.title
        activitySwitch.isChecked = source.active
        activitySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            source.active = isChecked
        }
    }
}