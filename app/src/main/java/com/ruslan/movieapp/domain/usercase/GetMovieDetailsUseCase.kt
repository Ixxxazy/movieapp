package com.ruslan.movieapp.domain.usercase

import com.ruslan.movieapp.domain.model.Movie
import com.ruslan.movieapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieDetailsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(movieId: Int): Flow<Result<Movie>> {
        return repository.getMovieDetails(movieId)
    }
}