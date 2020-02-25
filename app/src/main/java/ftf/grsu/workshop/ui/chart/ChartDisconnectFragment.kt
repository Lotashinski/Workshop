package ftf.grsu.workshop.ui.chart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ftf.grsu.workshop.R

class ChartDisconnectFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("chart.disconnect", "show")
        return inflater.inflate(R.layout.fragment_chart_disconnect, container, false)
    }
}
