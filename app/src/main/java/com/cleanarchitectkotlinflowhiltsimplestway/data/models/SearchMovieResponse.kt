package com.cleanarchitectkotlinflowhiltsimplestway.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class SearchMovieResponse(
  @SerializedName("Search") var Search: ArrayList<Movie> = arrayListOf(),
  @SerializedName("totalResults") var totalResults: String? = null,
  @SerializedName("Response") var Response: String? = null,
  @SerializedName("Error") var Error: String? = null
)

@Entity
data class Movie(
  @ColumnInfo(name = "Title")
  @SerializedName("Title") var Title: String? = null,
  @ColumnInfo(name = "Year")
  @SerializedName("Year") var Year: String? = null,
  @PrimaryKey
  @SerializedName("imdbID") var imdbID: String,
  @ColumnInfo(name = "Type")
  @SerializedName("Type") var Type: String? = null,
  @ColumnInfo(name = "Poster")
  @SerializedName("Poster") var Poster: String? = null,

  @ColumnInfo(name = "Favorite")
  var favorite: Boolean = false
)