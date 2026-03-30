package com.ruslan.movieapp.ui.moviedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruslan.movieapp.data.local.FavoriteMovieEntity
import com.ruslan.movieapp.domain.model.Movie
import com.ruslan.movieapp.domain.repository.FavoriteRepository
import com.ruslan.movieapp.domain.usercase.GetMovieDetailsUseCase
import com.ruslan.movieapp.domain.usercase.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val favoriteRepository: FavoriteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: Int = savedStateHandle.get<String>("movieId")?.toIntOrNull() ?: 0

    private val _uiState = MutableStateFlow(MovieDetailsState())
    val uiState: StateFlow<MovieDetailsState> = _uiState.asStateFlow()

    init {
        loadMovieDetails()
        checkFavoriteStatus()
    }

    private fun loadMovieDetails() {
        viewModelScope.launch {
            getMovieDetailsUseCase(movieId).catch { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }.collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            movie = result.data,
                            error = null
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private fun checkFavoriteStatus() {
        viewModelScope.launch {
            val isFav = favoriteRepository.isFavorite(movieId)
            _uiState.value = _uiState.value.copy(isFavorite = isFav)
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val current = uiState.value.isFavorite
            if (current) {
                favoriteRepository.removeFromFavorites(movieId)
            } else {
                val movie = uiState.value.movie
                if (movie != null) {
                    val entity = FavoriteMovieEntity(
                        movieId = movie.id,
                        title = movie.title,
                        originalTitle = movie.originalTitle,
                        overview = movie.overview,
                        posterPath = movie.posterPath,
                        backdropPath = movie.backdropPath,
                        releaseDate = movie.releaseDate,
                        voteAverage = movie.voteAverage,
                        voteCount = movie.voteCount,
                        genres = movie.genres
                    )
                    favoriteRepository.addToFavorites(entity)
                }
            }
            _uiState.value = _uiState.value.copy(isFavorite = !current)
        }
    }

    fun retry() {
        loadMovieDetails()
    }
}

data class MovieDetailsState(
    val isLoading: Boolean = false,
    val movie: Movie? = null,
    val error: String? = null,
    val isFavorite: Boolean = false
)