package com.ruslan.movieapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ruslan.movieapp.ui.moviedetails.MovieDetailsScreen
import com.ruslan.movieapp.ui.movieslist.MoviesListScreen

sealed class Screen(val route: String, val title: String, val icon: @Composable () -> Unit) {
    object Movies : Screen("movies", "Фильмы", { Icon(Icons.Default.Home, contentDescription = "Фильмы") })
    object Search : Screen("search", "Поиск", { Icon(Icons.Default.Search, contentDescription = "Поиск") })
    object Favorites : Screen("favorites", "Избранное", { Icon(Icons.Default.Favorite, contentDescription = "Избранное") })
    object Profile : Screen("profile", "Профиль", { Icon(Icons.Default.Person, contentDescription = "Профиль") })
}

@Composable
fun MovieAppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Movies.route
    ) {
        composable(Screen.Movies.route) {
            MoviesListScreen(
                onMovieClick = { movieId ->
                    navController.navigate("movie_details/$movieId")
                }
            )
        }

        composable("movie_details/{movieId}") { backStackEntry ->
            // Правильное извлечение movieId
            val movieIdStr = backStackEntry.arguments?.getString("movieId") ?: "0"
            val movieId = movieIdStr.toIntOrNull() ?: 0

            MovieDetailsScreen(
                movieId = movieId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Search.route) {
            Text("Поиск (в разработке)")
        }
        composable(Screen.Favorites.route) {
            Text("Избранное (в разработке)")
        }
        composable(Screen.Profile.route) {
            Text("Профиль (в разработке)")
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val screens = listOf(
        Screen.Movies,
        Screen.Search,
        Screen.Favorites,
        Screen.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(modifier = modifier) {
        screens.forEach { screen ->
            NavigationBarItem(
                icon = screen.icon,
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}