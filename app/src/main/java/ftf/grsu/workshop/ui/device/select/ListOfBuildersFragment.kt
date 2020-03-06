package ftf.grsu.workshop.ui.device.select


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import ftf.grsu.workshop.R
import ftf.grsu.workshop.ui.DeviceViewModel


class ListOfBuildersFragment : Fragment() {
    private val viewModel: DeviceViewModel by activityViewModels()
    private lateinit var _recycleView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_list_of_builders, container, false)

        viewModel.updateBuilders()

        _recycleView = root.findViewById(R.id.recycler_list_builders)

        viewModel.builders.observe(viewLifecycleOwner, Observer {
            _recycleView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter =
                    AdapterMeterBuilders(it) { meterBuilder -> viewModel.connect(meterBuilder) }
            }
        })

        return root
    }
}
