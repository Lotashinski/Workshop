package com.grsu.workshop.device.scanner

import androidx.fragment.app.Fragment
import com.grsu.workshop.R
import com.grsu.workshop.device.IUiAdapter
import com.grsu.workshop.ui.fragment.device.scanner.ScannerMainFragment
import com.grsu.workshop.ui.fragment.device.scanner.ScannerTitleFragment

class UiAdapter: IUiAdapter {


    override val title: Int
        get() = R.string.device_scanner

    override fun contentFragment(): Fragment {
        return ScannerMainFragment()
    }

    override fun titleFragment(): Fragment {
        return ScannerTitleFragment()
    }

}