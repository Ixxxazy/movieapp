package com.ruslan.movieapp.domain.repository

import com.ruslan.movieapp.data.preferences.FilterPreferences
import com.ruslan.movieapp.domain.model.Movie
import com.ruslan.movieapp.domain.usercase.Result
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getPopularMovies(page: Int, filters: FilterPreferences): Flow<Result<List<Movie>>>
    fun getMovieDetails(movieId: Int): Flow<Result<Movie>>
}