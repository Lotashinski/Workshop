package ftf.grsu.workshop.ui.device

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import ftf.grsu.workshop.R
import ftf.grsu.workshop.ui.DeviceViewModel
import ftf.grsu.workshop.ui.device.current.CurrentDeviceFragment
import ftf.grsu.workshop.ui.device.select.ListOfBuildersFragment

class DeviceFragment : Fragment() {

    private val viewModel: DeviceViewModel by activityViewModels()
    private val listOfBuildersFragment = ListOfBuildersFragment()
    private val currentDeviceFragment = CurrentDeviceFragment()

    private fun showList(): Unit = with(fragmentManager!!.beginTransaction()) {
        Log.d("device.fragment", "show list")
        replace(R.id.device_content, listOfBuildersFragment)
        commit()
    }

    private fun showCurrent(): Unit = with(fragmentManager!!.beginTransaction()) {
        Log.d("device.fragment", "show current")
        replace(R.id.device_content, currentDeviceFragment)
        commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_device, container, false)

        with(fragmentManager!!.beginTransaction()) {
            add(
                R.id.device_content, when (viewModel.isConnect.value ?: false) {
                    true -> currentDeviceFragment
                    false -> listOfBuildersFragment
                }
            )
            commit()
        }

        viewModel.isConnect.observe(this, Observer {
            when (it) {
                true -> showCurrent()
                false -> showList()
            }
        })

        return root
    }
}