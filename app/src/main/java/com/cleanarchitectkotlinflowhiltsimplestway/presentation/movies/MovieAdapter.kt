package com.cleanarchitectkotlinflowhiltsimplestway.presentation.movies

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cleanarchitectkotlinflowhiltsimplestway.R
import com.cleanarchitectkotlinflowhiltsimplestway.databinding.ItemMovieBinding
import com.cleanarchitectkotlinflowhiltsimplestway.domain.entities.MovieView
import com.dtv.starter.presenter.utils.extension.loadImageFitToImageViewWithCorder

class MovieAdapter(val onFavoriteChanged: OnFavoriteChangeListener) : ListAdapter<MovieView, MovieAdapter.MovieViewHolder>(MovieDiffCallback()) {

  class MovieViewHolder(val binding: ItemMovieBinding) : ViewHolder(binding.root)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
    return MovieViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_movie, parent, false))
  }

  override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
    holder.binding.movie = getItem(position)
    holder.binding.onFavoriteChanged = onFavoriteChanged
    holder.binding.executePendingBindings()
  }

}

interface OnFavoriteChangeListener {
  fun onFavoriteChange(movieView: MovieView)
}

@BindingAdapter("bindFavorite")
fun ImageView.bindFavorite(movieView: MovieView) {
  if (movieView.favorite) {
    alpha = 1f
  } else {
    alpha = 0.3f
  }
}

@BindingAdapter("bindMovieImage")
fun ImageView.bindMovieImage(movieView: MovieView) {
  loadImageFitToImageViewWithCorder(movieView.image)
}

class MovieDiffCallback : DiffUtil.ItemCallback<MovieView>() {
  override fun areItemsTheSame(oldItem: MovieView, newItem: MovieView): Boolean {
    return oldItem.id == newItem.id
  }

  override fun areContentsTheSame(oldItem: MovieView, newItem: MovieView): Boolean {
    return oldItem.image == newItem.image && oldItem.favorite == newItem.favorite
  }

}