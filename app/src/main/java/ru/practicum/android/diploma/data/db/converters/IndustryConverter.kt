package ru.practicum.android.diploma.data.db.converters

import ru.practicum.android.diploma.data.dto.IndustryDto
import ru.practicum.android.diploma.data.dto.IndustryFilterDto
import ru.practicum.android.diploma.domain.models.Industry

object IndustryConverter {
    fun map(industry: Industry): IndustryDto {
        return IndustryDto(
            id = industry.id,
            name = industry.name,
            industriesList = emptyList()
        )
    }

    fun map(industry: IndustryDto): Industry {
        return Industry(
            id = industry.id,
            name = industry.name,
            isSelected = false
        )
    }

    fun mapFilter(industry: Industry): IndustryFilterDto {
        return IndustryFilterDto(
            id = industry.id,
            name = industry.name,
            isSelected = industry.isSelected
        )
    }

    fun mapFilter(industry: IndustryFilterDto): Industry {
        return Industry(
            id = industry.id,
            name = industry.name,
            isSelected = industry.isSelected
        )
    }

    fun mapToList(dtoList: List<IndustryDto>): List<Industry> {
        val itemList = mutableListOf<Industry>()
        val list = mutableListOf<IndustryDto>()
        for (industry in dtoList) {
            if (industry.industriesList != null) {
                for (i in industry.industriesList!!) {
                    list.add(i)
                }
            }
            list.add(industry)
        }
        val industryList = list.map {
            Industry(id = it.id, name = it.name, isSelected = false)
        }
        itemList.addAll(industryList)
        return itemList
    }
}
