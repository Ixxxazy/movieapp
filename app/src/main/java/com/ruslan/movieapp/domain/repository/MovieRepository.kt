package com.ruslan.movieapp.domain.repository

import com.ruslan.movieapp.domain.model.Movie
import com.ruslan.movieapp.domain.usercase.Result
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getPopularMovies(page: Int): Flow<Result<List<Movie>>>
    fun getMovieDetails(movieId: Int): Flow<Result<Movie>>
}