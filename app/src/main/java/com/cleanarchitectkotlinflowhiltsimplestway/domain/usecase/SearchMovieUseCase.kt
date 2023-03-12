package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.models.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.entities.MovieView
import com.cleanarchitectkotlinflowhiltsimplestway.domain.exception.SearchMovieException
import com.cleanarchitectkotlinflowhiltsimplestway.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchMovieUseCase @Inject constructor(private val movieRepository: MovieRepository) : UseCase<List<MovieView>, SearchMovieUseCase.Params>() {

  class Params(val query: String, val page: Int)

  override fun buildFlow(param: Params): Flow<State<List<MovieView>>> {
    return flow {

      val moviesResponse = movieRepository.searchMovie(param.query, param.page)

      if (moviesResponse.Response?.equals("true", true) != true) {
        emit(State.ErrorState(SearchMovieException(message = moviesResponse.Error ?: "")))
        return@flow
      }

      val moviesModel = moviesResponse.Search
      movieRepository.insertMoviesToDatabase(moviesModel)

      val movies = moviesModel.map {
        MovieView(id = it.imdbID, it.Title ?: "", it.Poster ?: "", favorite = movieRepository.isMovieFavorite(it))
      }
      emit(State.DataState(movies))
    }
  }

}