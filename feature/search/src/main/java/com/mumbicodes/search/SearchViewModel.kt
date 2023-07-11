package com.mumbicodes.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mumbicodes.common.result.Result
import com.mumbicodes.domain.repository.SearchRepository
import com.mumbicodes.network.SearchAnimeQuery
import com.mumbicodes.network.SearchCharacterQuery
import com.mumbicodes.network.type.MediaFormat
import com.mumbicodes.network.type.MediaSort
import com.mumbicodes.network.type.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {
    private val _searchMainFilterUiState: MutableStateFlow<SearchType> =
        MutableStateFlow(SearchType.ANIME)
    val searchMainFilterUiState = _searchMainFilterUiState.asStateFlow()

    val characterSearchResultsState: StateFlow<CharacterSearchUiState> = searchCharacter(
        searchParam = ""
    ).map {
        when (it) {
            is Result.ApplicationError -> {
                CharacterSearchUiState.Error(it.errors.joinToString())
            }

            is Result.Failure -> {
                CharacterSearchUiState.Error(it.exception.message.toString())
            }

            Result.Loading -> {
                CharacterSearchUiState.Loading
            }

            is Result.Success -> {
                CharacterSearchUiState.CharacterResults(
                    data = it.data
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CharacterSearchUiState.Loading
    )

    val animeSearchResultsState: StateFlow<AnimeSearchUiState> = searchAnime(
        searchParam = ""
    ).map {
        when (it) {
            is Result.ApplicationError -> {
                AnimeSearchUiState.Error(it.errors.joinToString())
            }

            is Result.Failure -> {
                AnimeSearchUiState.Error(it.exception.message.toString())
            }

            Result.Loading -> {
                AnimeSearchUiState.Loading
            }

            is Result.Success -> {
                AnimeSearchUiState.AnimeResults(
                    data = it.data
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnimeSearchUiState.Loading
    )

    /**
     * Updates the main search item user is searching for
     * */
    fun updateSearchFilter(userSearchMainFilter: SearchType) {
        _searchMainFilterUiState.value = userSearchMainFilter
    }

    private fun searchCharacter(searchParam: String): Flow<Result<List<SearchCharacterQuery.Character>>> =
        searchRepository.searchCharacter(searchParam = searchParam)

    private fun searchAnime(searchParam: String): Flow<Result<List<SearchAnimeQuery.Medium>>> =
        searchRepository.searchAnime(
            searchParam = searchParam,
            type = MediaType.ANIME,
            sortList = listOf(
                MediaSort.TRENDING,
                MediaSort.POPULARITY,
                MediaSort.FORMAT,
                MediaSort.TYPE
            ),
            formatIn = listOf(
                MediaFormat.MOVIE,
                MediaFormat.MUSIC,
                MediaFormat.TV,
                MediaFormat.SPECIAL,
                MediaFormat.MANGA
            )
        )
}

enum class SearchType {
    ANIME, CHARACTER
}