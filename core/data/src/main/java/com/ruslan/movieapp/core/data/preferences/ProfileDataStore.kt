package com.ruslan.movieapp.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ruslan.movieapp.core.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "profile")

class ProfileDataStore(
    private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val FULL_NAME_KEY = stringPreferencesKey("full_name")
        private val POSITION_KEY = stringPreferencesKey("position")
        private val AVATAR_URI_KEY = stringPreferencesKey("avatar_uri")
        private val RESUME_URL_KEY = stringPreferencesKey("resume_url")
        private val REMINDER_TIME_KEY = stringPreferencesKey("reminder_time")
    }

    val profileFlow: Flow<UserProfile> = dataStore.data.map { preferences ->
        UserProfile(
            fullName = preferences[FULL_NAME_KEY] ?: "",
            position = preferences[POSITION_KEY] ?: "",
            avatarUri = preferences[AVATAR_URI_KEY],
            resumeUrl = preferences[RESUME_URL_KEY] ?: "",
            reminderTime = preferences[REMINDER_TIME_KEY] ?: ""
        )
    }

    suspend fun saveProfile(profile: UserProfile) {
        dataStore.edit { preferences ->
            preferences[FULL_NAME_KEY] = profile.fullName
            preferences[POSITION_KEY] = profile.position
            profile.avatarUri?.let { preferences[AVATAR_URI_KEY] = it }
                ?: preferences.remove(AVATAR_URI_KEY)
            preferences[RESUME_URL_KEY] = profile.resumeUrl
            preferences[REMINDER_TIME_KEY] = profile.reminderTime
        }
    }
}