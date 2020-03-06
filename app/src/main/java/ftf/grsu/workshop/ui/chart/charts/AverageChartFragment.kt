package ftf.grsu.workshop.ui.chart.charts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

import ftf.grsu.workshop.R
import ftf.grsu.workshop.device.Meter
import ftf.grsu.workshop.ui.DeviceViewModel

/**
 * A simple [Fragment] subclass.
 */
class AverageChartFragment : Fragment() {

    private val _viewModel : DeviceViewModel by activityViewModels()

    private lateinit var _root: View
    private lateinit var _averageBar: BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _root = inflater.inflate(R.layout.fragment_awerage_chart, container, false)
        _averageBar = _root.findViewById(R.id.average_chart)

        _viewModel.current.observe(viewLifecycleOwner, Observer {
            val meter = it
            meter.updateCounter.observe(viewLifecycleOwner, Observer {
                drawChart(meter)
            })
        })

        return _root
    }

    private fun drawChart(meter: Meter){
        val formatter =
            ValueFormatter(resources)

        val entries = ArrayList<BarEntry>()
        val sources = meter.sourcesPressure

        val activeSources = sources
            .filter { source -> source.active }

        val sourcesValue = activeSources
            .map { source -> source.value.value!! }

        val average = sourcesValue
            .average()
            .toFloat()

        activeSources
            .map { source -> source.value.value!! }
            .forEachIndexed { index, value ->
                entries.add(BarEntry(1F * index, value - average))
            }


        val barDataSet = BarDataSet(entries, meter.title)

        val channelsTitles = activeSources
            .map { t -> t.title }

        val c = _viewModel.colors
            .filter { entry -> channelsTitles.contains(entry.key) }

        barDataSet.setColors(c.values.toIntArray(), context)
        barDataSet.valueFormatter = formatter

        val barData = BarData(barDataSet)
        _averageBar.data = barData
        _averageBar.description.isEnabled = false

        barData.setValueTextSize(14F)
        barData.setValueTextColor(R.color.colorText)

        _averageBar.data = barData
        _averageBar.setFitBars(true)

        val legend = _averageBar.legend
        val legendWords = activeSources
            .map { source -> source.title }
            .map { source ->
                LegendEntry().apply {
                    label = source
                    formColor = resources.getColor(c[source]!!)
                }
            }

        legend.setCustom(legendWords)
        _averageBar.xAxis.isEnabled = false

        _averageBar.axisRight.isEnabled = false
        _averageBar.axisLeft.valueFormatter = formatter

        _averageBar.invalidate()
    }
}
