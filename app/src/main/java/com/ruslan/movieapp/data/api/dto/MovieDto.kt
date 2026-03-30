package com.ruslan.movieapp.data.api.dto

import com.google.gson.annotations.SerializedName

data class MovieDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("alternativeName") val alternativeName: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("year") val year: Int,
    @SerializedName("rating") val rating: RatingDto?,
    @SerializedName("poster") val poster: PosterDto?,
    @SerializedName("backdrop") val backdrop: BackdropDto?,
    @SerializedName("genres") val genres: List<GenreDto>?,
    @SerializedName("votes") val votes: VotesDto?
)

data class RatingDto(
    @SerializedName("kp") val kp: Double?,
    @SerializedName("imdb") val imdb: Double?
)

data class PosterDto(
    @SerializedName("url") val url: String?
)

data class BackdropDto(
    @SerializedName("url") val url: String?
)

data class GenreDto(
    @SerializedName("name") val name: String
)

data class VotesDto(
    @SerializedName("kp") val kp: Int?
)