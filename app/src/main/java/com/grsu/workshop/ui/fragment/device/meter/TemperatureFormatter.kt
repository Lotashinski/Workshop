package com.grsu.workshop.ui.fragment.device.meter

import android.content.res.Resources
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.grsu.workshop.R
import java.text.DecimalFormat

class TemperatureFormatter(
    private val res: Resources
) : ValueFormatter() {
    private val barFormat = DecimalFormat("###,#0.0")
    private val axisFormat = DecimalFormat("###,#0.0")

    override fun getBarLabel(barEntry: BarEntry?): String {
        val y : Float = barEntry?.y ?: 0F

        return barFormat.format(y)
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        var y : Float = value

        val prefix = when{
            y >= 0 -> ""
            else -> {
                y *= -1
                "-"
            }
        }
        val postFix = res.getString(R.string.celsius_short)

        return prefix + axisFormat.format(y) + " $postFix"
    }
}