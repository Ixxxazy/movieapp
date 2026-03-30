package com.ruslan.movieapp.data.api

import com.ruslan.movieapp.data.api.dto.MovieDto
import com.ruslan.movieapp.domain.model.Movie

object MovieMapper {
    fun mapToDomain(dto: MovieDto): Movie {
        return Movie(
            id = dto.id,
            title = dto.name,
            originalTitle = dto.alternativeName ?: dto.name,
            overview = dto.description ?: "Описание отсутствует",
            posterPath = dto.poster?.url,
            backdropPath = dto.backdrop?.url,
            releaseDate = dto.year.toString(),
            voteAverage = dto.rating?.kp ?: (dto.rating?.imdb ?: 0.0),
            voteCount = dto.votes?.kp ?: 0,
            genres = dto.genres?.map { it.name } ?: emptyList()
        )
    }
}