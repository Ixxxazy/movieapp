package com.ruslan.movieapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "filters")

@Singleton
class FilterDataStore @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val GENRE_KEY = stringPreferencesKey("genre")
        private val MIN_RATING_KEY = floatPreferencesKey("min_rating")
        private val YEAR_KEY = intPreferencesKey("year")
    }

    val filterFlow: Flow<FilterPreferences> = dataStore.data.map { preferences ->
        FilterPreferences(
            genre = preferences[GENRE_KEY] ?: "",
            minRating = preferences[MIN_RATING_KEY] ?: 0f,
            year = preferences[YEAR_KEY] ?: 0
        )
    }

    suspend fun saveFilters(genre: String, minRating: Float, year: Int) {
        dataStore.edit { preferences ->
            preferences[GENRE_KEY] = genre
            preferences[MIN_RATING_KEY] = minRating
            preferences[YEAR_KEY] = year
        }
    }

    suspend fun clearFilters() {
        dataStore.edit { preferences ->
            preferences.remove(GENRE_KEY)
            preferences.remove(MIN_RATING_KEY)
            preferences.remove(YEAR_KEY)
        }
    }
}