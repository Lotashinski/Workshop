package ftf.grsu.workshop.ui.device.info


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

import ftf.grsu.workshop.R
import ftf.grsu.workshop.device.Meter
import ftf.grsu.workshop.ui.DeviceViewModel

class DeviceInfoFragment : Fragment() {

    private val deviceViewModel: DeviceViewModel by activityViewModels()

    private lateinit var root: View

    private fun bindMeter(meter: Meter) {
        root.findViewById<TextView>(R.id.text_device_info_main)
            .text = meter.title
        root.findViewById<TextView>(R.id.text_device_info_additional)
            .text = meter.address

        meter.power.observe(this, Observer {
            val resId = when (it / 10) {
                in 90..100 -> R.drawable.ic_battery_full_black_24dp
                in 80..89 -> R.drawable.ic_battery_90_black_24dp
                in 70..79 -> R.drawable.ic_battery_80_black_24dp
                in 60..69 -> R.drawable.ic_battery_60_black_24dp
                in 50..59 -> R.drawable.ic_battery_50_black_24dp
                in 40..49 -> R.drawable.ic_battery_50_black_24dp
                in 30..39 -> R.drawable.ic_battery_30_black_24dp
                in 20..29 -> R.drawable.ic_battery_20_black_24dp
                in 10..19 -> R.drawable.ic_battery_20_black_24dp
                else -> R.drawable.ic_battery_alert_black_24dp
            }
            root.findViewById<ImageView>(R.id.image_device_battery).setImageResource(resId)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_device_info, container, false)

        deviceViewModel.current.observe(this, Observer { bindMeter(it) })

        return root
    }


}
