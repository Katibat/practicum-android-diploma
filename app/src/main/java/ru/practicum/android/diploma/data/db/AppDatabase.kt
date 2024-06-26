package ru.practicum.android.diploma.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.practicum.android.diploma.data.db.converters.ListOfDbConverter
import ru.practicum.android.diploma.data.db.dao.DictionaryDAO
import ru.practicum.android.diploma.data.db.dao.FavoritesDao
import ru.practicum.android.diploma.data.db.dao.models.CurrencyDictionaryEntity
import ru.practicum.android.diploma.data.db.dao.models.VacancyEntity

@Database(
    version = 1,
    entities = [VacancyEntity::class, CurrencyDictionaryEntity::class],
    exportSchema = false
)
@TypeConverters(ListOfDbConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoritesDao(): FavoritesDao

    abstract fun dictionaryDAO(): DictionaryDAO
}
