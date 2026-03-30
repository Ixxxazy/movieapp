package com.ruslan.movieapp.core.data.repository

import com.ruslan.movieapp.core.data.preferences.ProfileDataStore
import com.ruslan.movieapp.core.domain.model.UserProfile
import com.ruslan.movieapp.core.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileDataStore: ProfileDataStore
) : ProfileRepository {

    override fun getProfile(): Flow<UserProfile> = profileDataStore.profileFlow

    override suspend fun saveProfile(profile: UserProfile) {
        profileDataStore.saveProfile(profile)
    }
}