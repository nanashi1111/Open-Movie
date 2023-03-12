package com.cleanarchitectkotlinflowhiltsimplestway.presentation.favorite

import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.data.models.Movie
import com.cleanarchitectkotlinflowhiltsimplestway.data.models.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.entities.MovieView
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.ChangeMovieFavoriteUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.GetFavoriteMovieUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteMoviesViewModel @Inject constructor(
  private val changeMovieUseCase: ChangeMovieFavoriteUseCase,
  private val getFavoriteMovieUseCase: GetFavoriteMovieUseCase
) : BaseViewModel() {

  private val _favoriteMovies: MutableStateFlow<State<List<MovieView>>> = MutableStateFlow(State.LoadingState)
  val favoriteMovies: StateFlow<State<List<MovieView>>> = _favoriteMovies

  private val _changedFavoriteMovie = MutableSharedFlow<State<Movie>>()
  val changedFavoriteMovie: SharedFlow<State<Movie>> = _changedFavoriteMovie

  fun getFavoriteMovies() {
    viewModelScope.launch(Dispatchers.IO) {
      getFavoriteMovieUseCase.invoke(Unit).collectLatest { _favoriteMovies.emit(it) }
    }
  }

  fun changeMovieFavorite(movie: MovieView) {
    viewModelScope.launch(Dispatchers.IO) {
      changeMovieUseCase.invoke(ChangeMovieFavoriteUseCase.Params(movie.id, !movie.favorite)).collectLatest {
        _changedFavoriteMovie.emit(it)
      }
    }
  }
}