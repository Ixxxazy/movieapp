package com.ruslan.movieapp.ui.movieslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruslan.movieapp.data.cache.FilterBadgeCache
import com.ruslan.movieapp.data.preferences.FilterDataStore
import com.ruslan.movieapp.data.preferences.FilterPreferences
import com.ruslan.movieapp.domain.model.Movie
import com.ruslan.movieapp.domain.usercase.GetMoviesUseCase
import com.ruslan.movieapp.domain.usercase.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesListViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val filterDataStore: FilterDataStore,
    private val filterBadgeCache: FilterBadgeCache
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoviesListState())
    val uiState: StateFlow<MoviesListState> = _uiState.asStateFlow()

    private val _showFilterBadge = MutableStateFlow(false)
    val showFilterBadge: StateFlow<Boolean> = _showFilterBadge.asStateFlow()

    init {
        loadFilters()
    }

    private fun loadFilters() {
        viewModelScope.launch {
            filterDataStore.filterFlow.collect { filters ->
                _uiState.value = _uiState.value.copy(currentFilters = filters)
                val hasActiveFilters = filters.genre.isNotBlank() || filters.minRating > 0f || filters.year > 0
                _showFilterBadge.value = hasActiveFilters
                loadMovies(filters)
            }
        }
    }

    fun loadMovies(filters: FilterPreferences = _uiState.value.currentFilters) {
        viewModelScope.launch {
            getMoviesUseCase(page = 1, filters = filters).catch { e ->
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
                            movies = result.data,
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

    fun retry() {
        loadMovies()
    }
}

data class MoviesListState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val error: String? = null,
    val currentFilters: FilterPreferences = FilterPreferences()
)