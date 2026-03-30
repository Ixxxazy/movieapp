package com.ruslan.movieapp.domain.usercase

import com.ruslan.movieapp.domain.model.Movie
import com.ruslan.movieapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(page: Int = 1): Flow<Result<List<Movie>>> {
        return repository.getPopularMovies(page)
    }
}