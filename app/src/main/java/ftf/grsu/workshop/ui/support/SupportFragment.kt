package ftf.grsu.workshop.ui.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ftf.grsu.workshop.R

class SupportFragment : Fragment() {

    private lateinit var shareViewModel: SupportViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        shareViewModel =
            ViewModelProviders.of(this).get(SupportViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_support, container, false)
        val textView: TextView = root.findViewById(R.id.text_share)
        return root
    }
}