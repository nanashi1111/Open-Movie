package com.cleanarchitectkotlinflowhiltsimplestway.data.repository

import com.cleanarchitectkotlinflowhiltsimplestway.data.cached.AppDatabase
import com.cleanarchitectkotlinflowhiltsimplestway.data.models.Movie
import com.cleanarchitectkotlinflowhiltsimplestway.data.models.SearchMovieResponse
import com.cleanarchitectkotlinflowhiltsimplestway.data.remote.Api
import com.cleanarchitectkotlinflowhiltsimplestway.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(private val omdbApi: Api, private val appDatabase: AppDatabase) : MovieRepository {
  override suspend fun searchMovie(query: String, page: Int): SearchMovieResponse {
    return omdbApi.searchMovie(query = query, page = page)
  }

  override suspend fun getFavoriteMovies(): List<Movie> {
    return appDatabase.contactDao().getFavoriteMovies(true)
  }

  override suspend fun getMovieById(id: String): Movie? {
    return appDatabase.contactDao().getMovieById(id)
  }

  override suspend fun insertMoviesToDatabase(movies: List<Movie>) {
    appDatabase.contactDao().updateMovies(movies)
  }

  override suspend fun isMovieFavorite(movie: Movie): Boolean {
    return appDatabase.contactDao().getMovieById(movie.imdbID)?.favorite ?: false
  }

  override suspend fun setMovieFavorite(movieId: String, favorite: Boolean) {
    val movie = appDatabase.contactDao().getMovieById(movieId)
    movie?.let {
      it.favorite = favorite
      appDatabase.contactDao().updateMovie(it)
    }
  }

  override suspend fun getMovies(ids: List<String>): List<Movie> {
    return appDatabase.contactDao().getMovies(ids)
  }
}