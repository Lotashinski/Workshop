package com.grsu.workshop.ui.fragment.device.scanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.grsu.workshop.R

/**
 * A simple [Fragment] subclass.
 */
class ScannerTitleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scanner_title, container, false)
    }

}
