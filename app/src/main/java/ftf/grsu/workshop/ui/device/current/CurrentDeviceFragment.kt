package ftf.grsu.workshop.ui.device.current


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

import ftf.grsu.workshop.R
import ftf.grsu.workshop.device.Source
import ftf.grsu.workshop.ui.DeviceViewModel

class CurrentDeviceFragment : Fragment() {

    private val viewModel: DeviceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_current_device, container, false)

        viewModel.current.observe(viewLifecycleOwner, Observer {
            root.findViewById<FloatingActionButton>(R.id.button_disconnect)
                .setOnClickListener { viewModel.disconnect() }
            root.findViewById<RecyclerView>(R.id.reecucle_view_sources).apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = AdapterSources(it.sourcesPressure)
            }
        })

        return root
    }
}
