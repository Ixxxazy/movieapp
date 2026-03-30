package com.ruslan.movieapp.ui.movieslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruslan.movieapp.domain.model.Movie
import com.ruslan.movieapp.domain.usercase.GetMoviesUseCase
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
class MoviesListViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoviesListUiState())
    val uiState: StateFlow<MoviesListUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var isLoadingMore = false
    private var hasMorePages = true

    init {
        loadMovies()
    }

    fun loadMovies(isRefresh: Boolean = false) {
        if (isRefresh) {
            currentPage = 1
            hasMorePages = true
            _uiState.update { it.copy(isRefreshing = true, error = null) }
        } else if (_uiState.value.isLoading || isLoadingMore) {
            return
        }

        viewModelScope.launch {
            if (currentPage == 1 && !isRefresh) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            } else if (!isRefresh) {
                isLoadingMore = true
                _uiState.update { it.copy(isLoadingMore = true) }
            }

            getMoviesUseCase(currentPage)
                .catch { exception ->
                    val errorMessage = when (exception) {
                        is java.net.UnknownHostException -> "Нет подключения к интернету"
                        is java.net.SocketTimeoutException -> "Превышено время ожидания"
                        else -> "Ошибка загрузки: ${exception.message}"
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            isRefreshing = false,
                            error = errorMessage
                        )
                    }
                    isLoadingMore = false
                }
                .collect { result ->
                    when (result) {
                        is Result.Loading -> { }
                        is Result.Success -> {
                            val newMovies = if (currentPage == 1) {
                                result.data
                            } else {
                                _uiState.value.movies + result.data
                            }

                            _uiState.update {
                                it.copy(
                                    movies = newMovies,
                                    isLoading = false,
                                    isLoadingMore = false,
                                    isRefreshing = false,
                                    error = null
                                )
                            }

                            hasMorePages = result.data.isNotEmpty()
                            if (result.data.isNotEmpty()) {
                                currentPage++
                            }
                            isLoadingMore = false
                        }
                        is Result.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isLoadingMore = false,
                                    isRefreshing = false,
                                    error = result.message
                                )
                            }
                            isLoadingMore = false
                        }
                    }
                }
        }
    }

    fun retry() {
        if (_uiState.value.error != null) {
            currentPage = 1
            hasMorePages = true
            loadMovies()
        }
    }

    fun loadMore() {
        if (!_uiState.value.isLoading && !isLoadingMore && !_uiState.value.isRefreshing && hasMorePages) {
            loadMovies()
        }
    }

    fun refresh() {
        loadMovies(isRefresh = true)
    }
}

data class MoviesListUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val error: String? = null
)