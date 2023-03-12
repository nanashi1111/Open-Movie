package com.cleanarchitectkotlinflowhiltsimplestway.presentation.movies

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.data.models.State
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.FragmentSearchMovieBinding
import com.cleanarchitectkotlinflowhiltsimplestway.domain.entities.MovieView
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.base.BaseViewBindingFragment
import com.cleanarchitectkotlinflowhiltsimplestway.utils.extension.safeNavigate
import com.cleanarchitectkotlinflowhiltsimplestway.utils.view.EndlessRecyclerViewScrollListener
import com.dtv.starter.presenter.utils.extension.beGone
import com.dtv.starter.presenter.utils.extension.beVisibleIf
import com.dtv.starter.presenter.utils.extension.setSafeOnClickListener
import com.cleanarchitectkotlinflowhiltsimplestway.utils.log.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class SearchMovieFragment : BaseViewBindingFragment<FragmentSearchMovieBinding, SearchMovieViewModel>(FragmentSearchMovieBinding::inflate) {

  override val viewModel: SearchMovieViewModel by viewModels()

  private val movieAdapter = MovieAdapter(object : OnFavoriteChangeListener {
    override fun onFavoriteChange(movieView: MovieView) {
      viewModel.changeMovieFavorite(movieView)
    }
  })

  private val layoutManager: GridLayoutManager by lazy {
    GridLayoutManager(requireActivity(), 2)
  }

  private var appBarHeight = 0

  private val loadMoreListener: EndlessRecyclerViewScrollListener by lazy {
    val listener = EndlessRecyclerViewScrollListener(layoutManager)
    listener.setOnLoadMoreListener {
      viewModel.loadMore()
    }
    listener
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.moviesState.collectLatest {
          when (it) {
            is State.LoadingState -> {
              //viewBinding.pbLoading.beVisible()
              viewBinding.pbLoading.beVisibleIf(movieAdapter.currentList.isEmpty())
            }
            is State.DataState -> {
              viewBinding.pbLoading.beGone()
              val movies = if (it.data.clearOldData) {
                it.data.movie
              } else {
                val result = mutableListOf<MovieView>()
                result.addAll(movieAdapter.currentList)
                result.addAll(it.data.movie)
                result
              }
              movieAdapter.submitList(movies)
              loadMoreListener.setLoaded()
            }
            is State.ErrorState -> {
              viewBinding.pbLoading.beGone()
              loadMoreListener.setLoaded()
              Logger.d("Error: ${it.exception.message}")
            }
          }
        }
      }
    }

    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
        viewModel.changedFavoriteMovie.collectLatest { state ->
          if (state is State.DataState) {
            val currentList = mutableListOf<MovieView>()
            currentList.addAll(movieAdapter.currentList.map {
              it.copy()
            })
            currentList.firstOrNull { movie ->
              state.data.imdbID == movie.id
            }?.favorite = state.data.favorite
            movieAdapter.submitList(currentList)
          }
        }
      }
    }

  }

  override fun initView() {
    viewBinding.rvMovie.apply {
      val gridLayoutManager = GridLayoutManager(requireActivity(), 2)
      layoutManager = gridLayoutManager
      adapter = movieAdapter
      addOnScrollListener(loadMoreListener)
    }

    viewBinding.ivFavoriteMovies.setSafeOnClickListener {
      findNavController().safeNavigate(SearchMovieFragmentDirections.actionSearchMovieFragmentToFavoriteMoviesFragment())
    }

    viewBinding.etSearch.addTextChangedListener {
      if (it.toString().isNotEmpty()) {
        viewModel.submitNewQuery(it.toString())
      }
    }

    lifecycleScope.launch {
      delay(500)
      viewBinding.appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
        Logger.d("verticalOffset = $verticalOffset")
        val ratio = abs(verticalOffset * 1.0f / appBarHeight)
        viewBinding.tvTitle.alpha = 1 - ratio
        val startMargin = resources.getDimensionPixelSize(R.dimen._15sdp)
        val maxAdditionalMargin = resources.getDimensionPixelSize(R.dimen._50sdp) - startMargin
        val lp = viewBinding.etSearch.layoutParams as ConstraintLayout.LayoutParams
        var addtionalMargin = startMargin + (ratio) * maxAdditionalMargin
        if (addtionalMargin == 0f) {
          addtionalMargin = startMargin.toFloat()
        }
        lp.marginEnd = addtionalMargin.toInt()
        viewBinding.etSearch.layoutParams = lp
      }
    }

    viewBinding.appBarLayout.post {
      appBarHeight = viewBinding.appBarLayout.height
    }

    viewBinding.etSearch.requestFocus()

  }

  override fun onResume() {
    super.onResume()
    viewModel.fetchUpdatedMoviesInformation(movieAdapter.currentList.map { it.id })
  }

  override suspend fun subscribeData() {
  }
}