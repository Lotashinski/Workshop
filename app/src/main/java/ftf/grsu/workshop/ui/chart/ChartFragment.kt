package ftf.grsu.workshop.ui.chart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import ftf.grsu.workshop.R
import ftf.grsu.workshop.ui.DeviceViewModel

class ChartFragment : Fragment() {

    private val viewModel: DeviceViewModel by activityViewModels()

    private val connectFragment = ChartConnectFragment()
    private val disconnectFragment = ChartDisconnectFragment()

    private lateinit var root: View

    private fun showAlarm() = with(fragmentManager!!.beginTransaction()) {
        Log.d("device.chert", "show alarm")
        replace(R.id.frame_chart_layout, disconnectFragment)
        commit()
    }

    private fun showChart() = with(fragmentManager!!.beginTransaction()) {
        Log.d("device.chart", "show chart")
        replace(R.id.frame_chart_layout, connectFragment)
        commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_chart, container, false)

        with(fragmentManager!!.beginTransaction()) {
            add(
                R.id.frame_chart_layout, when (viewModel.isConnect.value ?: false) {
                    true -> connectFragment
                    false -> disconnectFragment
                }
            )
            commit()
        }

        viewModel.isConnect.observe(this, Observer {
            when(it){
                true-> showChart()
                false -> showAlarm()
            }
        })

        return root
    }


}