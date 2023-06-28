package com.mumbicodes.data.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.mumbicodes.common.result.Result
import com.mumbicodes.domain.repository.AnimeRepository
import com.mumbicodes.network.AnimeListQuery
import com.mumbicodes.network.AnimeQuery
import com.mumbicodes.network.RecommendationsQuery
import com.mumbicodes.network.type.MediaFormat
import com.mumbicodes.network.type.MediaSort
import com.mumbicodes.network.type.MediaType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapLatest

@OptIn(ExperimentalCoroutinesApi::class)
class AnimeRepositoryImpl(private val apolloClient: ApolloClient) : AnimeRepository {
    override fun getAnimeList(
        page: Int?,
        perPage: Int?,
        type: MediaType?,
        sortList: List<MediaSort>?,
        formatIn: List<MediaFormat>?
    ): Flow<Result<List<AnimeListQuery.Medium>>> {
        TODO("")
    }

    override fun getRecommendations(): Flow<Result<List<RecommendationsQuery.Media>>> {
        TODO("")
    }

    override fun getAnime(animeId: Int, page: Int?, perPage: Int?): Flow<Result<AnimeQuery.Media>> {
        val response = apolloClient.query(
            AnimeQuery(
                mediaId = Optional.present(animeId),
                page = Optional.presentIfNotNull(page),
                perPage = Optional.presentIfNotNull(perPage)
            )
        ).fetchPolicy(FetchPolicy.NetworkFirst)
            .toFlow()

        return response.mapLatest {
            if (it.hasErrors()) {
                Result.ApplicationError(it.errors!!)
            } else if (it.data != null) {
                Result.Success(it.data!!.Media!!)
            } else {
                error("Unknown error occurred. There was no data or errors received")
            }
        }.catch {
            Result.Failure(it)
        }
    }
}