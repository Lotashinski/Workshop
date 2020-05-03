package com.grsu.workshop.ui.fragment.device.meter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

import com.grsu.workshop.R

class MeterChartFragment : Fragment() {

    private val _meterViewModel by activityViewModels<MeterViewModel>()
    private lateinit var _root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _root = inflater.inflate(R.layout.fragment_meter_chart, container, false)



        _meterViewModel.tab.observe(viewLifecycleOwner, Observer {
            childFragmentManager.beginTransaction()
                .replace(R.id.meter_chart, it.fragment())
                .commit()
        })

        return _root
    }

}
