package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.models.Movie
import com.cleanarchitectkotlinflowhiltsimplestway.data.models.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChangeMovieFavoriteUseCase @Inject constructor(private val movieRepository: MovieRepository) : UseCase<Movie, ChangeMovieFavoriteUseCase.Params>() {

  class Params(val id: String, val favorite: Boolean)

  override fun buildFlow(param: Params): Flow<State<Movie>> {
    return flow {
      movieRepository.setMovieFavorite(param.id, param.favorite)
      val movie = movieRepository.getMovieById(param.id)
      if (movie == null) {
        emit(State.ErrorState(Throwable("Unexpected exception")))
      } else {
        emit(State.DataState(movie))
      }
    }
  }
}