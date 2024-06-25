package ru.practicum.android.diploma.data.preferences

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.practicum.android.diploma.domain.models.Filtration

class PreferencesProviderImpl(
    private val gson: Gson,
    private val prefs: SharedPreferences,
) : PreferencesProvider {
    override suspend fun saveFiltration(filtration: Filtration?) {
        if (filtration == null) {
            prefs.edit().putString(FILTRATION_LABEL, gson.toJson(null)).apply()
        } else {
            val filtrationNullCheck =
                filtration.salary.isNullOrEmpty() && filtration.area == null && filtration.industry == null
            if (filtrationNullCheck && !filtration.onlyWithSalary) {
                prefs.edit().putString(FILTRATION_LABEL, gson.toJson(null)).apply()
            } else {
                prefs.edit().putString(FILTRATION_LABEL, gson.toJson(filtration)).apply()
            }
        }
    }

    override suspend fun getFiltration(): Filtration? {
        val filtrationString = prefs.getString(FILTRATION_LABEL, "")
        val itemType = object : TypeToken<Filtration?>() {}.type
        val filtration = gson.fromJson<Filtration?>(filtrationString, itemType)
        return filtration
    }

    companion object {
        const val FILTRATION_LABEL = "FILTRATION_LABEL"
    }
}
