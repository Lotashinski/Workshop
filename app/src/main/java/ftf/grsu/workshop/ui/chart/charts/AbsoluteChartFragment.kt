package ftf.grsu.workshop.ui.chart.charts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import ftf.grsu.workshop.R
import ftf.grsu.workshop.device.ISource
import ftf.grsu.workshop.device.Meter
import ftf.grsu.workshop.ui.DeviceViewModel


/**
 * A simple [Fragment] subclass.
 */
class AbsoluteChartFragment : Fragment() {

    private val _viewModel: DeviceViewModel by activityViewModels()

    private lateinit var _root: View
    private lateinit var _chart: CombinedChart
    private lateinit var _valueFormatter: ValueFormatter
    private var _textChartSize: Float = 0f;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _root = inflater.inflate(R.layout.fragment_absolute_chart, container, false)

        _textChartSize = resources.getDimension(R.dimen.text_chart_size)
        _valueFormatter = ValueFormatter(resources)

        _chart = _root.findViewById(R.id.absolute_chart)
        _chart.description.isEnabled = false;
        _chart.drawOrder = arrayOf(DrawOrder.BAR, DrawOrder.LINE)
        _chart.axisLeft.valueFormatter = _valueFormatter
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

        _viewModel.current.observe(viewLifecycleOwner, Observer {
            val meter = it
            meter.updateCounter.observe(viewLifecycleOwner, Observer {
                drawChart(meter.sourcesPressure)
            })
        })

        return _root
    }

    private fun drawChart(sources: List<ISource>) {
        val activeSources = sources
            .filter { source -> source.active }

        val berEntries = ArrayList<BarEntry>()

        activeSources
            .forEachIndexed { index, source ->
                berEntries.add(
                    BarEntry(
                        index.toFloat(),
                        source.value.value!!.toFloat()
                    )
                )
            }

        val barDataSet = BarDataSet(berEntries, "")

        val titles = activeSources.map { source -> source.title }
        barDataSet.stackLabels = titles.toTypedArray()

        val colors = _viewModel.colors.filter { e -> titles.contains(e.key) }
            .map { e -> resources.getColor(e.value) }

        barDataSet.colors = colors
        barDataSet.label = resources.getString(R.string.chart_channels)
        barDataSet.valueFormatter = _valueFormatter
        barDataSet.valueTextSize = _textChartSize

        val average = activeSources
            .map { s -> s.value.value!! }
            .average()
            .toFloat()

        val lineData = LineData()
        val lineEntries = listOf(
            Entry(-0.5f, average),
            Entry( activeSources.size.toFloat() - 0.5f, average)
        )
        val lineDataSet = LineDataSet(lineEntries, resources.getString(R.string.chart_average))
        lineDataSet.color = resources.getColor(R.color.colorAverage)
        lineDataSet.lineWidth = 2.5f
        lineDataSet.setDrawCircles(false)
        lineDataSet.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter(){
            override fun getFormattedValue(value: Float): String = ""
        }
        lineData.addDataSet(lineDataSet)

        val cd = CombinedData()
        val barData = BarData(barDataSet)
        barData.setValueTextColor(R.color.colorText)

        cd.setData(barData)
        cd.setData(lineData)

        _chart.data = cd
        val xAxis = _chart.xAxis
        xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return titles[value.toInt() % titles.size]
            }
        }
        xAxis.axisMinimum = -0.5f
        xAxis.axisMaximum = activeSources.size.toFloat() - 0.5f
        xAxis.textSize = _textChartSize
        _chart.invalidate()
    }

}
