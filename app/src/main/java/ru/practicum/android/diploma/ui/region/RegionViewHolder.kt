package ru.practicum.android.diploma.ui.region

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.Region

class RegionViewHolder(
    itemView: View,
    private val onClick: (Region) -> Unit,
) : RecyclerView.ViewHolder(itemView) {
    private val regionName: TextView = itemView.findViewById(R.id.tvRegionName)

    fun bind(region: Region) {
        regionName.text = region.name
        itemView.setOnClickListener { onClick(region) }
    }
}
