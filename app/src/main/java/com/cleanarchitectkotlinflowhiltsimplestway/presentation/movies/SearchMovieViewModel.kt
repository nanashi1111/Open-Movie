package com.cleanarchitectkotlinflowhiltsimplestway.presentation.movies

import androidx.lifecycle.viewModelScope
import com.cleanarchitectkotlinflowhiltsimplestway.data.models.Movie
import com.cleanarchitectkotlinflowhiltsimplestway.data.models.State
import com.cleanarchitectkotlinflowhiltsimplestway.domain.entities.MovieView
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.ChangeMovieFavoriteUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.FetchUpdatedMoviesInformation
import com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase.SearchMovieUseCase
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchMovieViewModel @Inject constructor(
  private val searchMovieUseCase: SearchMovieUseCase,
  private val changeMovieUseCase: ChangeMovieFavoriteUseCase,
  private val fetchUpdatedMoviesInformation: FetchUpdatedMoviesInformation
) : BaseViewModel() {

  private val query = MutableStateFlow("")

  private var currentPage = 1
  private var canloadMore = true

  private var job: Job? = null

  private val _moviesState = MutableStateFlow<State<Movies>>(State.DataState(Movies()))
  val moviesState: StateFlow<State<Movies>> = _moviesState

  private val _changedFavoriteMovie = MutableSharedFlow<State<Movie>>()
  val changedFavoriteMovie: SharedFlow<State<Movie>> = _changedFavoriteMovie

  init {
    initListeners()
  }

  @OptIn(FlowPreview::class)
  private fun initListeners() {
    viewModelScope.launch(Dispatchers.IO) {
      query.debounce(500).distinctUntilChanged().collectLatest {
        if (it.isEmpty()) {
          return@collectLatest
        }
        canloadMore = true
        currentPage = 1
        searchMovie(it, currentPage, clearOldData = true)
      }
    }
  }

  fun changeMovieFavorite(movie: MovieView) {
    viewModelScope.launch(Dispatchers.IO) {
      changeMovieUseCase.invoke(ChangeMovieFavoriteUseCase.Params(movie.id, !movie.favorite)).collectLatest {
        _changedFavoriteMovie.emit(it)
      }
    }
  }


  private fun searchMovie(query: String, page: Int, clearOldData: Boolean) {
    if (!canloadMore || _moviesState.value is State.LoadingState) {
      return
    }
    job?.cancel()
    job = viewModelScope.launch(Dispatchers.IO) {
      searchMovieUseCase.invoke(SearchMovieUseCase.Params(query, page)).collectLatest {
        when (it) {
          is State.DataState -> {
            canloadMore = it.data.isNotEmpty()
            currentPage++
            _moviesState.emit(State.DataState(Movies(it.data, clearOldData)))
          }
          is State.LoadingState -> {
            _moviesState.emit(State.LoadingState)
          }
          is State.ErrorState -> {
            _moviesState.emit(State.ErrorState(it.exception))
          }
        }
        //val newData = mutableListOf<MovieView>()

      }
    }
  }

  fun loadMore() {
    searchMovie(query.value, currentPage, clearOldData = false)
  }

  fun submitNewQuery(query: String) {
    viewModelScope.launch { this@SearchMovieViewModel.query.emit(query) }
  }

  fun fetchUpdatedMoviesInformation(ids: List<String>) {
    viewModelScope.launch(Dispatchers.IO) {
      fetchUpdatedMoviesInformation.invoke(ids).collectLatest {
        if (it is State.DataState) {
          _moviesState.emit(State.DataState(Movies(it.data, true)))
        }
      }
    }
  }
}

data class Movies(val movie: List<MovieView> = emptyList(), val clearOldData: Boolean = true)

