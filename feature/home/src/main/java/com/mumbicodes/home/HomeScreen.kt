package com.mumbicodes.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onAnimeClicked: () -> Unit,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
) {
    val homeScreenUiStates: HomeScreenUiStates by homeScreenViewModel.uiState.collectAsStateWithLifecycle()
    val popularAnimeUiStates: PopularAnimeStates by homeScreenViewModel.popularUiState.collectAsStateWithLifecycle()
    Column() {
        when (homeScreenUiStates) {
            is HomeScreenUiStates.Animes -> {
                Column() {
                    Text(
                        text = "Recommended",
                        modifier = modifier.clickable {
                            onAnimeClicked()
                        }
                    )
                    Text(
                        text = (homeScreenUiStates as HomeScreenUiStates.Animes)
                            .recommended.first().media?.title?.english
                            ?: "It is empty",
                        modifier = modifier.clickable {
                            onAnimeClicked()
                        }
                    )

                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            is HomeScreenUiStates.Error -> {
                TODO()
            }

            HomeScreenUiStates.Loading -> {
                Text(
                    text = "This is the loading state",
                    modifier = modifier.clickable {
                        onAnimeClicked()
                    }
                )
            }
        }

        when (popularAnimeUiStates) {
            is PopularAnimeStates.PopularAnimes -> {
                Column() {
                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = "Popular:",
                        modifier = modifier.clickable {
                            onAnimeClicked()
                        }
                    )
                    Text(
                        text = (popularAnimeUiStates as PopularAnimeStates.PopularAnimes).popular.first().title?.english
                            ?: "It is empty",
                        modifier = modifier.clickable {
                            onAnimeClicked()
                        }
                    )
                }
            }

            is PopularAnimeStates.Error -> {
                TODO()
            }

            PopularAnimeStates.Loading -> {
                Text(
                    text = "This is the loading popular state",
                    modifier = modifier.clickable {
                        onAnimeClicked()
                    }
                )
            }
        }
    }
}