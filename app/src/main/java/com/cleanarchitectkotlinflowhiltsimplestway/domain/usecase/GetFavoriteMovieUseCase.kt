package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.models.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.entities.MovieView
import com.cleanarchitectkotlinflowhiltsimplestway.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetFavoriteMovieUseCase @Inject constructor(private val movieRepository: MovieRepository) : UseCase<List<MovieView>, Unit>() {
  override fun buildFlow(param: Unit): Flow<State<List<MovieView>>> {
    return flow {
      val favoriteMovies = movieRepository.getFavoriteMovies()
      val moviesView = favoriteMovies.map {
        MovieView(it.imdbID, it.Title ?: "", it.Poster ?: "", it.favorite)
      }
      emit(State.DataState(moviesView))
    }
  }
}