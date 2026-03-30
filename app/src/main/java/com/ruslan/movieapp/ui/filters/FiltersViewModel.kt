package com.ruslan.movieapp.ui.filters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruslan.movieapp.data.cache.FilterBadgeCache
import com.ruslan.movieapp.data.preferences.FilterDataStore
import com.ruslan.movieapp.data.preferences.FilterPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FiltersViewModel @Inject constructor(
    private val filterDataStore: FilterDataStore,
    private val filterBadgeCache: FilterBadgeCache
) : ViewModel() {

    val filters: Flow<FilterPreferences> = filterDataStore.filterFlow

    fun saveFilters(genre: String, minRating: Float, year: Int) {
        viewModelScope.launch {
            filterDataStore.saveFilters(genre, minRating, year)
            filterBadgeCache.setShowBadge(
                genre.isNotBlank() || minRating > 0f || year > 0
            )
        }
    }

    fun clearFilters() {
        viewModelScope.launch {
            filterDataStore.clearFilters()
            filterBadgeCache.setShowBadge(false)
        }
    }
}