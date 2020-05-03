package com.grsu.workshop.ui.fragment.device.meter

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

import com.grsu.workshop.R
import com.grsu.workshop.ui.activity.MainViewModel

class MeterTitleFragment : Fragment() {

    private val _mainViewModel by activityViewModels<MainViewModel>()
    private val _meterViewModel by activityViewModels<MeterViewModel>()
    private lateinit var _root: View
    private lateinit var _buttonClose: Button
    private lateinit var _batteryText: TextView
    private lateinit var _batteryImage: ImageView

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _root = inflater.inflate(R.layout.fragment_meter_title, container, false)

        _batteryText = _root.findViewById(R.id.text_battery)
        _batteryImage = _root.findViewById(R.id.image_battery)
        _buttonClose = _root.findViewById(R.id.button_close)
        _buttonClose.setOnClickListener {
            Log.d("fragment_meter_title", "call close()")
            _mainViewModel.disconnectDevice()
        }

        _meterViewModel.powerSource.observe(viewLifecycleOwner, Observer {
            _batteryText.text = "" + it.value

            if (it.isCharging) {
                _batteryImage.setImageResource(R.mipmap.ic_charch_battery_none)
                return@Observer
            }

            when {
                it.value > 20 -> _batteryImage.setImageResource(R.mipmap.ic_std_battery_none)
                else -> _batteryImage.setImageResource(R.mipmap.ic_warning_battery_none)
            }
        })

        return _root
    }

}
