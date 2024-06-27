package ru.practicum.android.diploma.data.db.converters

import ru.practicum.android.diploma.data.dto.ContactsDto
import ru.practicum.android.diploma.data.dto.KeySkillDto
import ru.practicum.android.diploma.data.dto.PhoneDto
import ru.practicum.android.diploma.data.dto.SalaryDto
import ru.practicum.android.diploma.data.dto.VacancyDto
import ru.practicum.android.diploma.domain.models.Contacts
import ru.practicum.android.diploma.domain.models.Phone
import ru.practicum.android.diploma.domain.models.Salary
import ru.practicum.android.diploma.domain.models.Vacancy

class VacancyDtoConverter {
    fun map(vacancyDto: VacancyDto): Vacancy {
        return with(vacancyDto) {
            Vacancy(
                id = id,
                vacancyName = vacancyName,
                companyName = employer.name,
                alternateUrl = alternateUrl,
                logoUrl = employer.logo?.original,
                area = area.name,
                employment = employment?.name,
                experience = experience?.name,
                salary = createSalary(salary),
                description = description,
                keySkills = extractKeySkills(keySkills),
                contacts = createContacts(contacts),
                comment = comment,
                schedule = schedule?.name,
                address = address?.fullAddress
            )
        }
    }

    private fun createSalary(salaryDto: SalaryDto?): Salary? {
        if (salaryDto == null) return null
        return Salary(
            currency = salaryDto.currency,
            from = salaryDto.from,
            gross = salaryDto.gross,
            to = salaryDto.to
        )
    }

    private fun createContacts(contactsDto: ContactsDto?): Contacts? {
        if (contactsDto == null) return null
        return Contacts(
            email = contactsDto.email,
            name = contactsDto.name,
            phones = contactsDto.phones?.map { createPhone(it) }
        )
    }

    private fun createPhone(phoneDto: PhoneDto?): Phone? {
        if (phoneDto == null) return null
        return Phone(
            city = phoneDto.city,
            comment = phoneDto.comment,
            country = phoneDto.country,
            number = phoneDto.number
        )
    }

    private fun extractKeySkills(keySkills: List<KeySkillDto>?): List<String?> {
        return keySkills?.map { it.name } ?: emptyList()
    }
}
