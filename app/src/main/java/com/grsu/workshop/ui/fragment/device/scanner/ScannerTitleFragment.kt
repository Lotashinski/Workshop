package com.grsu.workshop.ui.fragment.device.scanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.activityViewModels

import com.grsu.workshop.R

/**
 * A simple [Fragment] subclass.
 */
class ScannerTitleFragment : Fragment() {

    private val _viewModel by activityViewModels<ScannerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_scanner_title, container, false)

        val btn = root.findViewById<ImageButton>(R.id.btn_refresh)
        btn.setOnClickListener {
            _viewModel.updateAvailable()
        }

        return root
    }

}
