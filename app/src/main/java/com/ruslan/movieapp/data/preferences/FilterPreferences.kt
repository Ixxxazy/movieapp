package com.ruslan.movieapp.data.preferences

data class FilterPreferences(
    val genre: String = "",
    val minRating: Float = 0f,
    val year: Int = 0
)