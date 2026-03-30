package com.ruslan.movieapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruslan.movieapp.data.preferences.ProfileDataStore
import com.ruslan.movieapp.domain.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileDataStore: ProfileDataStore
) : ViewModel() {

    val profile: Flow<UserProfile> = profileDataStore.profileFlow

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            profileDataStore.saveProfile(profile)
        }
    }
}