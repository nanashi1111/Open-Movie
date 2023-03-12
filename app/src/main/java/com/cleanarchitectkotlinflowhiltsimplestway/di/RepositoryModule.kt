package com.cleanarchitectkotlinflowhiltsimplestway.di

import android.content.Context
import com.cleanarchitectkotlinflowhiltsimplestway.data.cached.AppDatabase
import com.cleanarchitectkotlinflowhiltsimplestway.data.remote.Api
import com.cleanarchitectkotlinflowhiltsimplestway.data.repository.MovieRepositoryImpl
import com.cleanarchitectkotlinflowhiltsimplestway.domain.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideMovieRepository(api: Api, appDatabase: AppDatabase): MovieRepository = MovieRepositoryImpl(api, appDatabase)
}