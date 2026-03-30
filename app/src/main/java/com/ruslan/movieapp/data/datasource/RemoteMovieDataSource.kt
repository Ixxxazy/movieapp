package com.ruslan.movieapp.data.datasource

import com.ruslan.movieapp.data.api.MovieApiService
import com.ruslan.movieapp.data.api.MovieMapper
import com.ruslan.movieapp.domain.model.Movie
import javax.inject.Inject

class RemoteMovieDataSource @Inject constructor(
    private val apiService: MovieApiService
) {
    suspend fun getPopularMovies(page: Int): List<Movie> {
        val response = apiService.getMovies(page = page)
        return response.movies.map { MovieMapper.mapToDomain(it) }
    }

    suspend fun getMovieDetails(movieId: Int): Movie {
        val response = apiService.getMovieById(movieId)
        return MovieMapper.mapToDomain(response)
    }
}