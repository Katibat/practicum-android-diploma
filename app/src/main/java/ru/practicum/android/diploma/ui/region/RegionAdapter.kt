package ru.practicum.android.diploma.ui.region

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.Region
import java.util.Locale

class RegionAdapter(
    private var regions: List<Region>,
    private val onClick: (Region) -> Unit,
    private val callback: RegionCountCallback
) : RecyclerView.Adapter<RegionViewHolder>() {

    private var allRegions: List<Region> = regions

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.region_view, parent, false)
        return RegionViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        val region = regions[position]
        holder.bind(region)
    }

    override fun getItemCount(): Int = regions.size

    fun updateRegions(newRegions: List<Region>) {
        allRegions = newRegions
        regions = newRegions
        notifyDataSetChanged()
        callback.onRegionCountChanged(regions.size)
    }

    fun filterRegions(query: String) {
        regions = if (query.isEmpty()) {
            allRegions
        } else {
            allRegions.filter {
                it.name.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault()))
            }
        }
        notifyDataSetChanged()
        callback.onRegionCountChanged(regions.size)
    }
}
