package com.ruslan.movieapp.core.domain.repository

import com.ruslan.movieapp.core.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfile(): Flow<UserProfile>
    suspend fun saveProfile(profile: UserProfile)
}