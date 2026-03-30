package com.ruslan.movieapp.data.repository

import com.ruslan.movieapp.data.datasource.RemoteMovieDataSource
import com.ruslan.movieapp.domain.model.Movie
import com.ruslan.movieapp.domain.repository.MovieRepository
import com.ruslan.movieapp.domain.usercase.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteMovieDataSource
) : MovieRepository {

    override fun getPopularMovies(page: Int): Flow<Result<List<Movie>>> = flow {
        emit(Result.Loading)
        try {
            val movies = remoteDataSource.getPopularMovies(page)
            emit(Result.Success(movies))
        } catch (e: IOException) {
            emit(Result.Error("Нет подключения к интернету"))
        } catch (e: Exception) {
            emit(Result.Error("Ошибка загрузки: ${e.message}"))
        }
    }

    override fun getMovieDetails(movieId: Int): Flow<Result<Movie>> = flow {
        emit(Result.Loading)
        try {
            val movie = remoteDataSource.getMovieDetails(movieId)
            emit(Result.Success(movie))
        } catch (e: IOException) {
            emit(Result.Error("Нет подключения к интернету"))
        } catch (e: Exception) {
            emit(Result.Error("Ошибка загрузки: ${e.message}"))
        }
    }
}