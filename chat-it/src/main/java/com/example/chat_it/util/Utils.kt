package com.example.chat_it.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.provider.OpenableColumns
import androidx.annotation.WorkerThread

internal object Utils {

    var isDebugBuild: Boolean = false

    fun setBuildType(context: Context) {
        isDebugBuild = (0 != (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE))
    }

}