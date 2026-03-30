package com.ruslan.movieapp.ui.moviedetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.ruslan.movieapp.ui.moviedetails.MovieDetailsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsScreen(
    movieId: Int,
    onBack: () -> Unit,
    viewModel: MovieDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали фильма") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                // Состояние: загрузка
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                // Состояние: ошибка
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "❌ ${uiState.error}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.retry() }) {
                            Text("Повторить")
                        }
                    }
                }
                // Состояние: фильм не найден
                uiState.movie == null -> {
                    Text(
                        text = "Фильм не найден",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                // Состояние: фильм загружен
                else -> {
                    MovieDetailsContent(movie = uiState.movie!!)
                }
            }
        }
    }
}

@Composable
private fun MovieDetailsContent(movie: com.ruslan.movieapp.domain.model.Movie) {
    // Формируем URL постера для Kinopoisk.dev
    val posterUrl = movie.posterPath?.let { path ->
        when {
            path.startsWith("http") -> path
            path.startsWith("/") -> "https://st.kp.yandex.net$path"
            else -> "https://st.kp.yandex.net/images/film_iphone/iphone360_${movie.id}.jpg"
        }
    } ?: "https://via.placeholder.com/300x450?text=No+Poster"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = posterUrl,
            contentDescription = movie.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(Color(0xFF2C2C2C)),
            contentScale = ContentScale.Crop
        )

        // Остальной код (ConstraintLayout) остаётся без изменений
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val (titleText, yearText, ratingText, genresText, overviewTitle, overviewText) = createRefs()

            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.constrainAs(titleText) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
            )

            Text(
                text = movie.releaseDate.take(4),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.constrainAs(yearText) {
                    top.linkTo(titleText.bottom, margin = 4.dp)
                    start.linkTo(titleText.start)
                }
            )

            Text(
                text = "⭐ ${movie.voteAverage}/10 (${movie.voteCount} оценок)",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.constrainAs(ratingText) {
                    top.linkTo(titleText.bottom, margin = 4.dp)
                    end.linkTo(parent.end)
                }
            )

            Text(
                text = movie.genres.joinToString(" • "),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.constrainAs(genresText) {
                    top.linkTo(yearText.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                }
            )

            Text(
                text = "Описание",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.constrainAs(overviewTitle) {
                    top.linkTo(genresText.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                }
            )

            Card(
                modifier = Modifier.constrainAs(overviewText) {
                    top.linkTo(overviewTitle.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}