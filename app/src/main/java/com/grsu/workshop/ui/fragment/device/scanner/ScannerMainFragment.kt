package com.grsu.workshop.ui.fragment.device.scanner

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.grsu.workshop.R
import com.grsu.workshop.device.scanner.ScannerDevice
import com.grsu.workshop.ui.activity.MainViewModel

/**
 * A simple [Fragment] subclass.
 */
class ScannerMainFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private val _mainViewModel by activityViewModels<MainViewModel>()
    private val _scannerViewModel by activityViewModels<ScannerViewModel>()

    private lateinit var _root: View
    private lateinit var _swipe: SwipeRefreshLayout
    private lateinit var _listView: RecyclerView


    override fun onRefresh() {
        _scannerViewModel.updateAvailable()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("scanner_fragment", "create")
        _root = inflater.inflate(R.layout.fragment_scanner_main, container, false)
        _swipe = _root.findViewById(R.id.swipe_container_select_device)
        _swipe.setOnRefreshListener(this)
        _listView = _root.findViewById(R.id.recycler_select_device)
        _listView.setHasFixedSize(true)

        _swipe.isRefreshing = true
        _scannerViewModel.isLoad.observe(viewLifecycleOwner, Observer {
            _swipe.isRefreshing = it
        })

        _scannerViewModel.builders.observe(viewLifecycleOwner, Observer { list ->
            _listView.layoutManager = LinearLayoutManager(context)
            _listView.adapter = DeviceBuilderAdapter(list) {
                if (_mainViewModel.isLoad.value == false) {
                    _mainViewModel.connectDevice(it)
                }
            }
        })

        onRefresh()

        val device = _mainViewModel.device.value!!

        if (device is ScannerDevice)
            _scannerViewModel.bindDevice(device)

        return _root
    }

}
