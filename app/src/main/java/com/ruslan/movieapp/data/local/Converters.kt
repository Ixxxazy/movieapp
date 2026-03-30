package com.ruslan.movieapp.data.local

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromGenresList(genres: List<String>): String {
        return genres.joinToString(",")
    }

    @TypeConverter
    fun toGenresList(genresString: String): List<String> {
        return if (genresString.isEmpty()) emptyList() else genresString.split(",")
    }
}