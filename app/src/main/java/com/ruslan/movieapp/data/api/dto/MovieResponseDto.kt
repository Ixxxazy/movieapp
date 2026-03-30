package com.ruslan.movieapp.data.api.dto

import com.google.gson.annotations.SerializedName

data class MovieResponseDto(
    @SerializedName("docs") val movies: List<MovieDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("pages") val pages: Int
)