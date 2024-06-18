package ru.practicum.android.diploma.ui.country

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.Country

class CountryViewHolder(
    itemView: View,
    private val onClick: (Country) -> Unit,
) : RecyclerView.ViewHolder(itemView) {
    private val countryName: TextView = itemView.findViewById(R.id.tvCountryName)

    fun bind(country: Country) {
        countryName.text = country.name
        itemView.setOnClickListener {
            onClick(country)
        }
    }
}
