package com.ruslan.movieapp.di

import android.content.Context
import com.ruslan.movieapp.data.local.FavoriteDatabase
import com.ruslan.movieapp.data.local.FavoriteMovieDao
import com.ruslan.movieapp.data.repository.FavoriteRepositoryImpl
import com.ruslan.movieapp.domain.repository.FavoriteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFavoriteDatabase(@ApplicationContext context: Context): FavoriteDatabase {
        return FavoriteDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: FavoriteDatabase): FavoriteMovieDao {
        return database.favoriteDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteRepository(dao: FavoriteMovieDao): FavoriteRepository {
        return FavoriteRepositoryImpl(dao)
    }
}