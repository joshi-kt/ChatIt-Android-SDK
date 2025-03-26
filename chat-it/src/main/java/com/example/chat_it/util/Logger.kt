package com.example.chat_it.util

import android.os.Build
import android.util.Log
import com.example.chat_it.init.InstanceHandler

object Logger {

    private const val LOGGER_TAG = "ChatItLogger"

    enum class LogType {
        DEBUG,
        ERROR
    }

    fun log(
        tag : String,
        logType: LogType,
        message : String
    ) {
        if (isLogsEnabled()) {
            when(logType){
                LogType.DEBUG -> {
                    Log.d(LOGGER_TAG, "$tag $message")
                }
                LogType.ERROR -> {
                    Log.e(LOGGER_TAG, "$tag $message")
                }
            }
        }
    }

    fun isLogsEnabled() : Boolean {
        val isDebugLogsEnabled = InstanceHandler.instance.logConfig.onDebugBuild
        val isReleaseLogsEnabled = InstanceHandler.instance.logConfig.onReleaseBuild
        return when(Utils.isDebugBuild) {
            true -> isDebugLogsEnabled
            false -> isReleaseLogsEnabled
        }
    }

}