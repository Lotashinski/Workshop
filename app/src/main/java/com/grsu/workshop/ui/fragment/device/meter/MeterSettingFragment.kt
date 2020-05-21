package com.grsu.workshop.ui.fragment.device.meter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.grsu.workshop.R

class MeterSettingFragment : Fragment() {

    private lateinit var _root: View
    private lateinit var _averageButton: RadioButton
    private lateinit var _absoluteButton: RadioButton
    private lateinit var _temperatureButton: RadioButton
    private lateinit var _recyclerView: RecyclerView
    private val _meterViewModel by activityViewModels<MeterViewModel>()

    @ExperimentalUnsignedTypes
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _root = inflater.inflate(R.layout.fragment_meter_setting, container, false)
        _averageButton = _root.findViewById(R.id.radio_button_average)
        _absoluteButton = _root.findViewById(R.id.radio_button_absolute)
        _temperatureButton = _root.findViewById(R.id.radio_button_temperature)
        _recyclerView = _root.findViewById(R.id.recycler_channel_set)
        _recyclerView.setHasFixedSize(true)

        _absoluteButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                _meterViewModel.selectTab(MeterViewModel.Tab.ABSOLUTE)
        }

        _temperatureButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                _meterViewModel.selectTab(MeterViewModel.Tab.TEMPERATURE)
        }

        _averageButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                _meterViewModel.selectTab(MeterViewModel.Tab.AVERAGE)
        }

        _meterViewModel.meter.observe(viewLifecycleOwner, Observer {
            _recyclerView.layoutManager = LinearLayoutManager(context)
            _recyclerView.adapter = ChannelAdapter(
                it.bmpSources, listOf(
                    resources.getString(R.string.channel1),
                    resources.getString(R.string.channel2),
                    resources.getString(R.string.channel3),
                    resources.getString(R.string.channel4),
                    resources.getString(R.string.channel5),
                    resources.getString(R.string.channel6)
                )
            )
        })

        return _root
    }

}
