package com.ruslan.movieapp.ui.filters

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ruslan.movieapp.data.preferences.FilterPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersScreen(
    onBack: () -> Unit,
    viewModel: FiltersViewModel = hiltViewModel()
) {
    val filters by viewModel.filters.collectAsStateWithLifecycle(
        initialValue = FilterPreferences()
    )
    var genre by remember { mutableStateOf(filters.genre) }
    var minRating by remember { mutableStateOf(filters.minRating) }
    var year by remember { mutableStateOf(filters.year) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Фильтры") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = {
                        viewModel.clearFilters()
                        onBack()
                    }) {
                        Text("Сбросить")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("Жанр") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Например: драма, комедия") }
            )

            Text("Рейтинг: ${"%.1f".format(minRating)}")
            Slider(
                value = minRating,
                onValueChange = { minRating = it },
                valueRange = 0f..10f,
                steps = 20,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = if (year == 0) "" else year.toString(),
                onValueChange = { year = it.toIntOrNull() ?: 0 },
                label = { Text("Год") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Например: 2024") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.saveFilters(genre, minRating, year)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Применить")
            }
        }
    }
}