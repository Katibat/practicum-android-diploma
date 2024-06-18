package ru.practicum.android.diploma.data.db.converters

import ru.practicum.android.diploma.data.db.dao.models.ContactsEntity
import ru.practicum.android.diploma.data.db.dao.models.PhoneEntity
import ru.practicum.android.diploma.data.db.dao.models.SalaryEntity
import ru.practicum.android.diploma.data.db.dao.models.VacancyEntity
import ru.practicum.android.diploma.domain.models.Contacts
import ru.practicum.android.diploma.domain.models.Phone
import ru.practicum.android.diploma.domain.models.Salary
import ru.practicum.android.diploma.domain.models.Vacancy

class VacancyEntityConverter {
    fun map(vacancyEntity: VacancyEntity): Vacancy {
        return with(vacancyEntity) {
            Vacancy(
                id = id,
                vacancyName = vacancyName,
                companyName = companyName,
                alternateUrl = alternateUrl,
                logoUrl = logoUrl,
                area = area,
                employment = employment,
                experience = experience,
                salary = createSalary(salary),
                description = description,
                keySkills = keySkills,
                contacts = createContacts(contacts),
                comment = comment,
                schedule = schedule,
                address = address
            )
        }
    }

    private fun createSalary(salaryEntity: SalaryEntity?): Salary? {
        if (salaryEntity == null) return null
        return Salary(
            currency = salaryEntity.currency,
            from = salaryEntity.from,
            gross = salaryEntity.gross,
            to = salaryEntity.to
        )
    }

    private fun createContacts(contactsEntity: ContactsEntity?): Contacts? {
        if (contactsEntity == null) return null
        return Contacts(
            email = contactsEntity.email,
            name = contactsEntity.name,
            phones = contactsEntity.phones?.map { createPhone(it) }
        )
    }

    private fun createPhone(phoneEntity: PhoneEntity?): Phone? {
        if (phoneEntity == null) return null
        return Phone(
            city = phoneEntity.city,
            comment = phoneEntity.comment,
            country = phoneEntity.country,
            number = phoneEntity.number
        )
    }

    fun map(vacancy: Vacancy): VacancyEntity {
        return with(vacancy) {
            VacancyEntity(
                id = id,
                vacancyName = vacancyName,
                companyName = companyName,
                alternateUrl = alternateUrl,
                logoUrl = logoUrl,
                area = area,
                employment = employment,
                experience = experience,
                salary = createSalaryEntity(salary),
                description = description,
                keySkills = keySkills,
                contacts = createContactsEntity(contacts),
                comment = comment,
                schedule = schedule,
                address = address
            )
        }
    }

    private fun createSalaryEntity(salary: Salary?): SalaryEntity? {
        if (salary == null) return null
        return SalaryEntity(
            currency = salary.currency,
            from = salary.from,
            gross = salary.gross,
            to = salary.to
        )
    }

    private fun createContactsEntity(contacts: Contacts?): ContactsEntity? {
        if (contacts == null) return null
        return ContactsEntity(
            email = contacts.email,
            name = contacts.name,
            phones = contacts.phones?.map { createPhoneEntity(it) }
        )
    }

    private fun createPhoneEntity(phone: Phone?): PhoneEntity? {
        if (phone == null) return null
        return PhoneEntity(
            city = phone.city,
            comment = phone.comment,
            country = phone.country,
            number = phone.number
        )
    }
}
