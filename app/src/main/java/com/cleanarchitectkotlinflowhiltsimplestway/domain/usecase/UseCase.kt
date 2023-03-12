package com.cleanarchitectkotlinflowhiltsimplestway.domain.usecase

import com.cleanarchitectkotlinflowhiltsimplestway.data.models.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

abstract class UseCase<Output, Params> {
  operator fun invoke(param: Params): Flow<State<Output>> {
    return buildFlow(param)
      /*.retryWhen { cause, attempt ->
        cause is HttpException && attempt < 1
      }*/
      .onStart {
        emit(State.LoadingState)
      }.catch { cause: Throwable ->
        cause.printStackTrace()
        emit(State.ErrorState(cause))
      }
      .flowOn(Dispatchers.IO)
  }

  abstract fun buildFlow(param: Params): Flow<State<Output>>
}
