package com.ruslan.movieapp.data.api

import com.ruslan.movieapp.data.api.dto.MovieDto
import com.ruslan.movieapp.data.api.dto.MovieResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("v1.4/movie")
    suspend fun getMovies(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("sortField") sortField: String = "rating.kp",
        @Query("sortType") sortType: String = "-1",
        @Query("notNullFields") notNullFields: String = "poster.url"
    ): MovieResponseDto

    @GET("v1.4/movie/{id}")
    suspend fun getMovieById(
        @Path("id") id: Int
    ): MovieDto
}