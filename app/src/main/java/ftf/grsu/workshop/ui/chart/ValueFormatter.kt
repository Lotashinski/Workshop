package ftf.grsu.workshop.ui.chart

import android.content.res.Resources
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import ftf.grsu.workshop.R
import java.text.DecimalFormat

class ValueFormatter(
    private val res: Resources
) : ValueFormatter() {
    private val barFormat = DecimalFormat("###,##0.0")
    private val axisFormat = DecimalFormat("###,##0")

    override fun getBarLabel(barEntry: BarEntry?): String {
        var y : Float = barEntry?.y ?: 0F

        if (y > 1_000_000F)
            y /= 1_000_000F
        else if (y > 1_000F)
            y /= 1_000F

        return barFormat.format(y)
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        var y : Float = value

        val postFix: String = when {
            y >= 1_000_000F -> res.getString(R.string.mega)
            y >= 1_000F && y < 1_000_000F -> res.getString(R.string.kilo)
            else -> ""
        } + res.getString(R.string.pascal_short)

        if (y >= 1_000_000F)
            y /= 1_000_000F
        else if (y >= 1_000F)
            y /= 1_000F

        return axisFormat.format(y) + " $postFix"
    }
}