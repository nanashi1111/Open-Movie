package com.cleanarchitectkotlinflowhiltsimplestway.domain.repository

import com.cleanarchitectkotlinflowhiltsimplestway.data.models.Movie
import com.cleanarchitectkotlinflowhiltsimplestway.data.models.SearchMovieResponse

interface MovieRepository {
  suspend fun searchMovie(query: String, page: Int): SearchMovieResponse
  suspend fun getFavoriteMovies(): List<Movie>
  suspend fun getMovieById(id: String): Movie?
  suspend fun insertMoviesToDatabase(movies: List<Movie>)
  suspend fun isMovieFavorite(movie: Movie): Boolean
  suspend fun setMovieFavorite(movieId: String, favorite: Boolean)
  suspend fun getMovies(ids: List<String>): List<Movie>
}