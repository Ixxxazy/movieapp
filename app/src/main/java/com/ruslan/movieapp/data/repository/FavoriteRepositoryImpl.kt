package com.ruslan.movieapp.data.repository

import com.ruslan.movieapp.data.local.FavoriteMovieDao
import com.ruslan.movieapp.data.local.FavoriteMovieEntity
import com.ruslan.movieapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteMovieDao
) : FavoriteRepository {

    override fun getAllFavorites(): Flow<List<FavoriteMovieEntity>> {
        return favoriteDao.getAllFavorites()
    }

    override suspend fun addToFavorites(movie: FavoriteMovieEntity) {
        favoriteDao.insert(movie)
    }

    override suspend fun removeFromFavorites(movieId: Int) {
        favoriteDao.delete(movieId)
    }

    override suspend fun isFavorite(movieId: Int): Boolean {
        return favoriteDao.isFavorite(movieId)
    }
}