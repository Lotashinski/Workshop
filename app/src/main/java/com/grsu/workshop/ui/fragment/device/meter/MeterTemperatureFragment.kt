package com.grsu.workshop.ui.fragment.device.meter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter

import com.grsu.workshop.R
import com.grsu.workshop.device.meter.IBmpSource
import kotlin.properties.Delegates

/**
 * A simple [Fragment] subclass.
 */
class MeterTemperatureFragment : Fragment() {

    private val _materViewModel by activityViewModels<MeterViewModel>()
    private lateinit var _root: View
    private lateinit var _chart: CombinedChart
    private lateinit var _formatter: ValueFormatter
    private var _textChartSize by Delegates.notNull<Float>()


    @ExperimentalUnsignedTypes
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _root = inflater.inflate(R.layout.fragment_meter_temperature, container, false)
        _chart = _root.findViewById(R.id.chart_temperature)

        _textChartSize = resources.getDimension(R.dimen.chart_text_size)
        _formatter = TemperatureFormatter(resources)
        _chart.axisLeft.valueFormatter = _formatter
        _chart.description.isEnabled = false;
        _chart.drawOrder = arrayOf(CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE)
        _chart.axisLeft.textSize = _textChartSize

        val xAxis = _chart.xAxis
        xAxis.granularity = 1f;
        xAxis.position = XAxis.XAxisPosition.BOTTOM;
        _chart.axisRight.isEnabled = false

        val legend = _chart.legend
        legend.isWordWrapEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.textSize = _textChartSize

        _materViewModel.sources.observe(viewLifecycleOwner, Observer {
            draw(it)
        })

        return _root
    }

    private val _titles = mapOf(
        1 to "A",
        2 to "B",
        3 to "C",
        4 to "D",
        5 to "E",
        6 to "F"
    )

    @ExperimentalUnsignedTypes
    private fun draw(sources: List<IBmpSource>){
        if (sources.count() == 0)
            return

        val berEntries = ArrayList<BarEntry>()

        sources
            .forEachIndexed { index, source ->
                Log.d("d", "" + source.temperature)
                berEntries.add(
                    BarEntry(
                        index.toFloat(),
                        source.temperature.toFloat() / 10
                    )
                )
            }

        val barDataSet = BarDataSet(berEntries, "")

        val titles = sources.map { source -> _titles[source.uid.toInt()] ?: ""}
        barDataSet.stackLabels = titles.toTypedArray()

        val colors = _materViewModel.colors.filter { e -> titles.contains(e.key) }
            .map { e -> resources.getColor(e.value) }

        barDataSet.colors = colors
        barDataSet.label = resources.getString(R.string.chart_channels)
        barDataSet.valueFormatter = _formatter
        barDataSet.valueTextSize = _textChartSize

        val cd = CombinedData()
        val barData = BarData(barDataSet)
        barData.setValueTextColor(R.color.color_text)

        cd.setData(barData)

        _chart.data = cd
        val xAxis = _chart.xAxis
        xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return titles[value.toInt() % titles.size]
            }
        }
        xAxis.axisMinimum = -0.5f
        xAxis.axisMaximum = sources.size.toFloat() - 0.5f
        xAxis.textSize = _textChartSize
        _chart.invalidate()
    }

}
