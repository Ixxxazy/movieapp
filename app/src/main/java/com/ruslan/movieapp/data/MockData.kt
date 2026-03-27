package com.ruslan.movieapp.data

import com.ruslan.movieapp.data.model.Movie

object MockData {
    val movies = listOf(
        Movie(
            id = 1,
            title = "Побег из Шоушенка",
            originalTitle = "The Shawshank Redemption",
            posterPath = null,
            backdropPath = null,
            overview = "Два заключённых находят дружбу и находят искупление за десятилетия, находя надежду и смысл жизни за решёткой.",
            releaseDate = "1994-10-14",
            voteAverage = 9.3,
            voteCount = 25000,
            genres = listOf("Драма")
        ),
        Movie(
            id = 2,
            title = "Крёстный отец",
            originalTitle = "The Godfather",
            posterPath = null,
            backdropPath = null,
            overview = "Глава мафиозной семьи передаёт контроль своему сыну, который неохотно втягивается в мир преступности.",
            releaseDate = "1972-03-24",
            voteAverage = 9.2,
            voteCount = 19000,
            genres = listOf("Драма", "Криминал")
        ),
        Movie(
            id = 3,
            title = "Тёмный рыцарь",
            originalTitle = "The Dark Knight",
            posterPath = null,
            backdropPath = null,
            overview = "Бэтмен с помощью лейтенанта Гордона и прокурора Дента борется с хаосом, который сеет Джокер.",
            releaseDate = "2008-07-18",
            voteAverage = 9.0,
            voteCount = 27000,
            genres = listOf("Экшн", "Драма", "Криминал")
        ),
        Movie(
            id = 4,
            title = "Криминальное чтиво",
            originalTitle = "Pulp Fiction",
            posterPath = null,
            backdropPath = null,
            overview = "Переплетающиеся истории киллера, боксёра, гангстера и его жены в Лос-Анджелесе.",
            releaseDate = "1994-10-14",
            voteAverage = 8.9,
            voteCount = 21000,
            genres = listOf("Криминал", "Драма")
        ),
        Movie(
            id = 5,
            title = "Властелин колец: Возвращение короля",
            originalTitle = "The Lord of the Rings: The Return of the King",
            posterPath = null,
            backdropPath = null,
            overview = "Гэндальф и Арагорн ведут мир людей против армии Саурона, а Фродо и Сэм приближаются к Роковой горе.",
            releaseDate = "2003-12-17",
            voteAverage = 8.9,
            voteCount = 19000,
            genres = listOf("Фэнтези", "Приключения")
        )
    )
}