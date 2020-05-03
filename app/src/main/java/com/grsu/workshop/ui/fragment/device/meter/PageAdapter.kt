package com.grsu.workshop.ui.fragment.device.meter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PageAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    private val _content =
        listOf({MeterChartFragment()}, {MeterSettingFragment()})

    override fun getItemCount(): Int {
        return _content.count()
    }

    override fun createFragment(position: Int): Fragment {
        return _content[position]()
    }
}