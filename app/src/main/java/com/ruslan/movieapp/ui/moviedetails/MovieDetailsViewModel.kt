package com.ruslan.movieapp.ui.moviedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruslan.movieapp.domain.model.Movie
import com.ruslan.movieapp.domain.usercase.GetMovieDetailsUseCase
import com.ruslan.movieapp.domain.usercase.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: Int = savedStateHandle.get<String>("movieId")?.toIntOrNull() ?: 0

    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    init {
        loadMovieDetails()
    }

    fun loadMovieDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getMovieDetailsUseCase(movieId)
                .catch { exception ->
                    val errorMessage = when (exception) {
                        is java.net.UnknownHostException -> "Нет подключения к интернету"
                        is java.net.SocketTimeoutException -> "Превышено время ожидания"
                        else -> "Ошибка загрузки: ${exception.message}"
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                    }
                }
                .collect { result ->
                    when (result) {
                        is Result.Loading -> { }
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    movie = result.data,
                                    error = null
                                )
                            }
                        }
                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
        }
    }

    fun retry() {
        if (_uiState.value.error != null) {
            loadMovieDetails()
        }
    }
}

data class MovieDetailsUiState(
    val isLoading: Boolean = false,
    val movie: Movie? = null,
    val error: String? = null
)