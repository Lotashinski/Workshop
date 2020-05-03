package com.grsu.workshop.ui.fragment.device.meter

import android.content.res.Resources
import java.text.DecimalFormat
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.grsu.workshop.R

class CustomFormatter(
    private val res: Resources
) : ValueFormatter() {
    private val barFormat = DecimalFormat("###,##0.00")
    private val axisFormat = DecimalFormat("###,##0")

    override fun getBarLabel(barEntry: BarEntry?): String {
        val y : Float = barEntry?.y ?: 0F

        return barFormat.format(y / 1000)
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

        val postFix: String = when {
            y >= 1_000_000F -> res.getString(R.string.mega)
            y >= 1_000F && y < 1_000_000F -> res.getString(R.string.kilo)
            else -> ""
        } + res.getString(R.string.pascal_short)

        if (y >= 1_000_000F)
            y /= 1_000_000F
        else if (y >= 1_000F)
            y /= 1_000F

        return prefix + axisFormat.format(y) + " $postFix"
    }
}