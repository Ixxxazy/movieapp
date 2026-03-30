package com.ruslan.movieapp.data.datasource

import android.util.Log
import com.ruslan.movieapp.data.api.MovieApiService
import com.ruslan.movieapp.data.api.MovieMapper
import com.ruslan.movieapp.data.preferences.FilterPreferences
import com.ruslan.movieapp.domain.model.Movie
import javax.inject.Inject

class RemoteMovieDataSource @Inject constructor(
    private val apiService: MovieApiService
) {
    suspend fun getPopularMovies(
        page: Int,
        filters: FilterPreferences
    ): List<Movie> {
        Log.d("MovieFilters", "Filters: genre=${filters.genre}, minRating=${filters.minRating}, year=${filters.year}")

        val response = apiService.getMovies(
            page = page,
            limit = 20,
            genre = filters.genre.takeIf { it.isNotBlank() },
            minRating = if (filters.minRating > 0) filters.minRating else null,
            year = if (filters.year > 0) filters.year else null
        )

        val allMovies = response.movies.map { MovieMapper.mapToDomain(it) }

        val filteredByRating = if (filters.minRating > 0) {
            allMovies.filter { it.voteAverage >= filters.minRating }
        } else {
            allMovies
        }

        Log.d("MovieFilters", "API returned ${allMovies.size} movies, after rating: ${filteredByRating.size}")
        return filteredByRating
    }

    suspend fun getMovieDetails(movieId: Int): Movie {
        val response = apiService.getMovieById(movieId)
        return MovieMapper.mapToDomain(response)
    }
}