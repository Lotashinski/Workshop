package com.grsu.workshop.ui.fragment.device.meter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

import com.grsu.workshop.R
import com.grsu.workshop.device.meter.Meter
import com.grsu.workshop.ui.activity.MainViewModel

class MeterMainFragment : Fragment() {

    private val _mainViewModel by activityViewModels<MainViewModel>()
    private val _meterViewModel by activityViewModels<MeterViewModel>()

    private lateinit var _root: View

    @ExperimentalUnsignedTypes
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _root = inflater.inflate(R.layout.fragment_meter_main, container, false)

        val device = _mainViewModel.device.value!!
        if (device is Meter)
            _meterViewModel.bindMeter(device)

        val tabs = _root.findViewById<TabLayout>(R.id.meter_tab)
        val content = _root.findViewById<ViewPager2>(R.id.meter_content)

        val titles = resources.getStringArray(R.array.tabs)
        content.adapter = PageAdapter(this)

        TabLayoutMediator(tabs, content) { tab, position -> tab.text = titles[position] }
            .attach()

        return _root
    }

}
