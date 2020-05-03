package com.grsu.workshop.device.meter

import androidx.fragment.app.Fragment
import com.grsu.workshop.device.IUiAdapter
import com.grsu.workshop.ui.fragment.device.meter.MeterMainFragment
import com.grsu.workshop.ui.fragment.device.meter.MeterTitleFragment

class UiAdapter : IUiAdapter {

    override val title: Int = com.grsu.workshop.R.string.device_pressure_meter

    override fun contentFragment(): Fragment {
        return MeterMainFragment()
    }

    override fun titleFragment(): Fragment {
        return MeterTitleFragment()
    }
}