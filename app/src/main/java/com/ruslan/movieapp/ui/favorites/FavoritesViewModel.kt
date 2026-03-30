package com.ruslan.movieapp.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruslan.movieapp.data.local.FavoriteMovieEntity
import com.ruslan.movieapp.domain.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    val favorites: Flow<List<FavoriteMovieEntity>> = favoriteRepository.getAllFavorites()

    fun removeFromFavorites(movieId: Int) {
        viewModelScope.launch {
            favoriteRepository.removeFromFavorites(movieId)
        }
    }
}