package com.cleanarchitectkotlinflowhiltsimplestway.di


import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.cleanarchitectkotlinflowhiltsimplestway.BuildConfig
import com.cleanarchitectkotlinflowhiltsimplestway.data.remote.Api
import com.cleanarchitectkotlinflowhiltsimplestway.presentation.App
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

  @Singleton
  @Provides
  fun provideApplication(@ApplicationContext app: Context): App {
    return app as App
  }

  @Provides
  @Singleton
  fun provideRetrofit(client: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl(BuildConfig.OMDB_BASE_URL).client(client)
      .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
      .build()
  }

  private val READ_TIMEOUT = 30
  private val WRITE_TIMEOUT = 30
  private val CONNECTION_TIMEOUT = 10
  private val CACHE_SIZE_BYTES = 10 * 1024 * 1024L // 10 MB

  @Provides
  @Singleton
  fun provideOkHttpClient(
    headerInterceptor: Interceptor,
    chuckInterceptor: ChuckerInterceptor,
    cache: Cache
  ): OkHttpClient {
    val okHttpClientBuilder = OkHttpClient().newBuilder()
    okHttpClientBuilder.connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
    okHttpClientBuilder.readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
    okHttpClientBuilder.writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
    okHttpClientBuilder.cache(cache)
    okHttpClientBuilder.addInterceptor(headerInterceptor)
    okHttpClientBuilder.addInterceptor(chuckInterceptor)
    okHttpClientBuilder.addInterceptor(HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    })
    return okHttpClientBuilder.build()
  }

  @Provides
  @Singleton
  fun provideChuckInterceptor(@ApplicationContext app: Context): ChuckerInterceptor {
    val chuckerCollector = ChuckerCollector(
      context = app,
      showNotification = true,
      retentionPeriod = RetentionManager.Period.ONE_HOUR
    )
    return ChuckerInterceptor.Builder(app)
      .collector(chuckerCollector)
      .maxContentLength(250_000L)
      .alwaysReadResponseBody(true)
      .build()
  }

  @Provides
  @Singleton
  fun provideHeaderInterceptor(): Interceptor {
    return Interceptor {
      val requestBuilder = it.request().newBuilder()
      val newRequestWithApiKey = it.request()
        .url()
        .newBuilder()
        .addQueryParameter("apikey", BuildConfig.OMDB_API_KEY)
        .build()
      //requestBuilder.addHeader("Authorization", BuildConfig.UNSPLASH_ACCESS_KEY)
      requestBuilder.url(newRequestWithApiKey)
      it.proceed(requestBuilder.build())
    }
  }


  @Provides
  @Singleton
  internal fun provideCache(context: Context): Cache {
    val httpCacheDirectory = File(context.cacheDir.absolutePath, "HttpCache")
    return Cache(httpCacheDirectory, CACHE_SIZE_BYTES)
  }


  @Provides
  @Singleton
  fun provideContext(application: App): Context {
    return application.applicationContext
  }

  @Provides
  @Singleton
  fun provideApi(retrofit: Retrofit): Api {
    return retrofit.create(Api::class.java)
  }

}
