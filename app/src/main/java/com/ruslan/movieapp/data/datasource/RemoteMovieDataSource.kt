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
        Log.d("MovieFilters", "=== REMOTE DATA SOURCE ===")
        Log.d("MovieFilters", "Filters: genre=${filters.genre}, minRating=${filters.minRating}, year=${filters.year}")

        // Запрашиваем фильмы без minRating в API (чтобы получить больше данных)
        val response = apiService.getMovies(
            page = page,
            limit = 20,
            genre = filters.genre.takeIf { it.isNotBlank() },
            minRating = null,  // ← не передаём в API, фильтруем в коде
            year = if (filters.year > 0) filters.year else null
        )

        val allMovies = response.movies.map { MovieMapper.mapToDomain(it) }
        Log.d("MovieFilters", "API returned ${allMovies.size} movies")

        // Фильтруем по рейтингу в коде (>= minRating)
        val filteredByRating = if (filters.minRating > 0) {
            allMovies.filter { it.voteAverage >= filters.minRating }
        } else {
            allMovies
        }
        Log.d("MovieFilters", "After rating filter (>= ${filters.minRating}): ${filteredByRating.size} movies")

        // Фильтруем по жанру в коде (если нужно)
        val filteredByGenre = if (filters.genre.isNotBlank()) {
            filteredByRating.filter { movie ->
                movie.genres.any { genre -> genre.contains(filters.genre, ignoreCase = true) }
            }
        } else {
            filteredByRating
        }
        Log.d("MovieFilters", "After genre filter: ${filteredByGenre.size} movies")

        return filteredByGenre
    }

    suspend fun getMovieDetails(movieId: Int): Movie {
        val response = apiService.getMovieById(movieId)
        return MovieMapper.mapToDomain(response)
    }
}