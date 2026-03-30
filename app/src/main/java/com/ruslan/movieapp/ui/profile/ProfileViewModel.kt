package com.ruslan.movieapp.ui.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruslan.movieapp.data.preferences.ProfileDataStore
import com.ruslan.movieapp.domain.model.UserProfile
import com.ruslan.movieapp.utils.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileDataStore: ProfileDataStore,
    private val application: Application
) : ViewModel() {

    private val TAG = "Notification"

    val profile: Flow<UserProfile> = profileDataStore.profileFlow

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            profileDataStore.saveProfile(profile)
            Log.d(TAG, "Profile saved, reminderTime=${profile.reminderTime}")

            if (profile.reminderTime.isNotBlank()) {
                val userName = profile.fullName.ifEmpty { "Друг" }
                NotificationScheduler.scheduleReminder(
                    application,
                    profile.reminderTime,
                    userName
                )
            } else {
                NotificationScheduler.cancelReminder(application)
            }
        }
    }
}