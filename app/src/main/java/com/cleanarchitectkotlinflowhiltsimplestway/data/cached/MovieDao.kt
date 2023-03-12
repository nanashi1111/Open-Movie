package com.cleanarchitectkotlinflowhiltsimplestway.data.cached

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cleanarchitectkotlinflowhiltsimplestway.data.models.Movie

@Dao
interface MovieDao {

  @Query("select * from Movie where imdbID = :id")
  fun getMovieById(id: String): Movie?

  @Query("select * from Movie where favorite = :favorite")
  fun getFavoriteMovies(favorite: Boolean): List<Movie>

  @Query("select * from Movie where imdbID in (:ids)")
  fun getMovies(ids: List<String>): List<Movie>

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun updateMovies(movies: List<Movie>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun updateMovie(movie: Movie)

  @Query("delete from Movie where imdbID = :id")
  fun removeMovie(id: String)
}