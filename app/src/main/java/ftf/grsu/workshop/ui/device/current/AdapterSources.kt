package ftf.grsu.workshop.ui.device.current

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ftf.grsu.workshop.R
import ftf.grsu.workshop.device.Source

class AdapterSources(
    private val sources: List<Source>
) : RecyclerView.Adapter<ViewHolderSource>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSource {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_source_item, parent, false)
        return ViewHolderSource(root)
    }

    override fun getItemCount(): Int = sources.size

    override fun onBindViewHolder(holder: ViewHolderSource, position: Int) =
        holder.setSource(sources[position])
}