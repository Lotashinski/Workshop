package ftf.grsu.workshop.ui.chart


import android.os.Bundle
import android.util.Log
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

class ChartConnectFragment : Fragment() {

    private val deviceViewModel: DeviceViewModel by activityViewModels()
    private val chartViewModel: ChartDataViewModel by activityViewModels()

    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("chart.connect", "show")
        root = inflater.inflate(R.layout.fragment_chart_connect, container, false)

        deviceViewModel.current.observe(viewLifecycleOwner, Observer {
            drawChart(it)
            val meter = it
            meter.updateCounter.observe(viewLifecycleOwner, Observer { drawChart(meter) })
        })

        return root
    }

    private val colors = mapOf(
        "A" to R.color.colorA,
        "B" to R.color.colorB,
        "C" to R.color.colorC,
        "D" to R.color.colorD,
        "E" to R.color.colorE,
        "F" to R.color.colorF
    )

    private fun drawChart(meter: Meter) {
        val chart: BarChart = root.findViewById(R.id.bar_chart)

        val formatter = ValueFormatter(resources)

        val entries = ArrayList<BarEntry>()
        val sources = meter.sources

        val activeSources = sources
            .filter { source -> source.active }

        activeSources
            .forEachIndexed { index, source ->
                val longVal: Long = source.value.value ?: 0L
                val floatVal: Float = 1F * longVal
                entries.add(BarEntry(1F * index, floatVal))
            }

        val barDataSet = BarDataSet(entries, meter.title)

        val channelsTitles = activeSources
            .map { t -> t.title }

        val colors = this.colors
            .filter { entry -> channelsTitles.contains(entry.key) }

        barDataSet.setColors(colors.values.toIntArray(), context)
        barDataSet.valueFormatter = formatter

        val barData = BarData(barDataSet)
        chart.data = barData
        chart.description.isEnabled = false

        barData.setValueTextSize(14F)
        barData.setValueTextColor(R.color.colorText)

        chart.data = barData
        chart.setFitBars(true)

        val legend = chart.legend
        val legendWords = activeSources
            .map { source -> source.title }
            .map { source ->
                LegendEntry().apply {
                    label = source
                    formColor = resources.getColor(colors[source]!!)
                }
            }
        legend.setCustom(legendWords)
        chart.xAxis.isEnabled = false

        chart.axisRight.isEnabled = false
        chart.axisLeft.valueFormatter = formatter

        chart.invalidate()
    }

}
