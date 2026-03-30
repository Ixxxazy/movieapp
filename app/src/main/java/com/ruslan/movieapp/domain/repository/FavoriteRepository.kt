package com.ruslan.movieapp.domain.repository

import com.ruslan.movieapp.data.local.FavoriteMovieEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllFavorites(): Flow<List<FavoriteMovieEntity>>
    suspend fun addToFavorites(movie: FavoriteMovieEntity)
    suspend fun removeFromFavorites(movieId: Int)
    suspend fun isFavorite(movieId: Int): Boolean
}