package com.ruslan.movieapp.core.data.di

import android.content.Context
import com.ruslan.movieapp.core.data.preferences.ProfileDataStore
import com.ruslan.movieapp.core.data.repository.ProfileRepositoryImpl
import com.ruslan.movieapp.core.domain.repository.ProfileRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideProfileDataStore(@ApplicationContext context: Context): ProfileDataStore {
        return ProfileDataStore(context)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(dataStore: ProfileDataStore): ProfileRepository {
        return ProfileRepositoryImpl(dataStore)
    }
}