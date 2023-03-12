package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.models.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.entities.MovieView
import com.cleanarchitectkotlinflowhiltsimplestway.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FetchUpdatedMoviesInformation @Inject constructor(private val movieRepository: MovieRepository) : UseCase<List<MovieView>, List<String>>() {
  override fun buildFlow(param: List<String>): Flow<State<List<MovieView>>> {
    return flow {
      val result = mutableListOf<MovieView>()
      param.forEach { id ->
        movieRepository.getMovieById(id)?.let { movie ->
          result.add(
            MovieView(
              movie.imdbID, movie.Title ?: "", movie.Poster ?: "", movie.favorite
            )
          )
        }
      }
      emit(State.DataState(result))
    }
  }
}