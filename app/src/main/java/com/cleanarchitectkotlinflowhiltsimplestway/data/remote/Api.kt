package com.cleanarchitectkotlinflowhiltsimplestway.data.remote

import com.cleanarchitectkotlinflowhiltsimplestway.data.models.SearchMovieResponse
import retrofit2.http.*

interface Api {
  @GET("/")
  suspend fun searchMovie(@Query("s") query: String, @Query("type") type: String = "Movie", @Query("page") page: Int): SearchMovieResponse
}