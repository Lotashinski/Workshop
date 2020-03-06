package ftf.grsu.workshop.ui.chart.charts

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ftf.grsu.workshop.R

class FragmentPagerAdapter(
    context: Context,
    fm : FragmentManager
) : FragmentPagerAdapter(fm) {
    private val _fabTitles = context.resources.getStringArray(R.array.chart_pages);
    private val _fabs = listOf({AbsoluteChartFragment()}, {AverageChartFragment()})

    override fun getPageTitle(position: Int): CharSequence? = _fabTitles[position]

    override fun getItem(position: Int): Fragment = _fabs[position]()

    override fun getCount(): Int = 2
}