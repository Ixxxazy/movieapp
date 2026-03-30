package com.ruslan.movieapp.di

import android.content.Context
import com.ruslan.movieapp.data.cache.FilterBadgeCache
import com.ruslan.movieapp.data.preferences.FilterDataStore
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
    fun provideFilterDataStore(@ApplicationContext context: Context): FilterDataStore {
        return FilterDataStore(context)
    }

    @Provides
    @Singleton
    fun provideFilterBadgeCache(): FilterBadgeCache {
        return FilterBadgeCache()
    }
}