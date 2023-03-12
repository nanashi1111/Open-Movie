package com.cleanarchitectkotlinflowhiltsimplestway.utils.log

import com.cleanarchitectkotlinflowhiltsimplestway.BuildConfig


object Logger {

  private const val LOG = BuildConfig.LOGGABLE

  /**
   * Log debug

   * @param message message to log
   */
  fun d(message: String) {
    if (LOG) {
      com.orhanobut.logger.Logger.d(message)
    }
  }

}