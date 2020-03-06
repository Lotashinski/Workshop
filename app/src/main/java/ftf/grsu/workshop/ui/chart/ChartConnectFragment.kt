package ftf.grsu.workshop.ui.chart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

import ftf.grsu.workshop.R
import ftf.grsu.workshop.ui.chart.charts.FragmentPagerAdapter

class ChartConnectFragment : Fragment() {
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("chart.connect", "show")
        root = inflater.inflate(R.layout.fragment_chart_connect, container, false)

        val pagerAdapter = FragmentPagerAdapter(requireContext(), childFragmentManager)
        val viewPager: ViewPager = root.findViewById(R.id.chart_view_pager)
        viewPager.adapter = pagerAdapter
        val tabs: TabLayout = root.findViewById(R.id.chart_tabs)
        tabs.setupWithViewPager(viewPager)

        return root
    }
}
