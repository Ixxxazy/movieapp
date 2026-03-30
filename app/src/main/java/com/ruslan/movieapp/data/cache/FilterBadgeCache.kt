package com.ruslan.movieapp.data.cache

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FilterBadgeCache @Inject constructor() {
    private var showBadge: Boolean = false

    fun shouldShowBadge(): Boolean = showBadge

    fun setShowBadge(value: Boolean) {
        showBadge = value
    }
}