package com.cleanarchitectkotlinflowhiltsimplestway.data.cached

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cleanarchitectkotlinflowhiltsimplestway.data.models.Movie

@Database(entities = [Movie::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
  abstract fun contactDao(): MovieDao
}