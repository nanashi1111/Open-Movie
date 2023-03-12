package com.cleanarchitectkotlinflowhiltsimplestway.presentation.favorite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.cleanarchitectkotlinflowhiltsimplestway.data.models.State
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentFavoriteMoviesBinding
import com.cleanarchitectkotlinflowhiltsimplestway.domain.entities.MovieView
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.movies.MovieAdapter
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.movies.OnFavoriteChangeListener
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigateUp
import com.dtv.starter.presenter.utils.extension.beVisibleIf
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteMoviesFragment : BaseViewBindingFragment<FragmentFavoriteMoviesBinding, FavoriteMoviesViewModel>(FragmentFavoriteMoviesBinding::inflate) {

  override val viewModel: FavoriteMoviesViewModel by viewModels()

  private val movieAdapter = MovieAdapter(object : OnFavoriteChangeListener {
    override fun onFavoriteChange(movieView: MovieView) {
      viewModel.changeMovieFavorite(movieView)
    }
  })

  override fun initView() {
    viewBinding.ivBack.setSafeOnClickListener { findNavController().safeNavigateUp() }
    viewBinding.rvMovie.apply {
      val gridLayoutManager = GridLayoutManager(requireActivity(), 2)
      layoutManager = gridLayoutManager
      adapter = movieAdapter
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.changedFavoriteMovie.collectLatest { state ->
          if (state is State.DataState) {
            val currentList = mutableListOf<MovieView>()
            currentList.addAll(movieAdapter.currentList.map {
              it.copy()
            }.filter {
              it.id != state.data.imdbID
            })
            movieAdapter.submitList(currentList)
            viewBinding.tvEmpty.beVisibleIf(currentList.isEmpty())
          }
        }
      }
    }

    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.favoriteMovies.collectLatest {
          if (it is State.DataState) {
            movieAdapter.submitList(it.data)
            viewBinding.tvEmpty.beVisibleIf(it.data.isEmpty())
          }
        }
      }
    }

    viewModel.getFavoriteMovies()
  }

  override suspend fun subscribeData() {
  }
}