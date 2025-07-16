package com.example.chat_it.data.handler

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import androidx.annotation.WorkerThread
import com.example.chat_it.data.TaskExecutor
import com.example.chat_it.init.InstanceHandler
import com.example.chat_it.model.Message
import com.example.chat_it.util.CLOSE_CHAT_IT_TASK
import com.example.chat_it.util.Logger
import com.example.chat_it.util.MAX_AUDIO_SIZE
import com.example.chat_it.util.MAX_IMAGE_SIZE
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

internal object AudioMessageHelper {

    private const val AUDIO_FOLDER_NAME = "Chat It Audios"
    private const val TAG = "AudioMessageHandler"

    @WorkerThread
    fun saveAudioToDevice(
        audioUri: Uri,
        context: Context,
        message: Message
    ) : String {

        return AttachmentHelper.saveAttachmentToDevice(
            context,
            AUDIO_FOLDER_NAME,
            message,
            audioUri
        )

    }

    fun addAudioMetadata(
        uploadedUri: String? = null,
        message: Message,
        context: Context,
        audioUri: Uri? = null,
        filePath : String? = null,
        fileName : String? = null
    ) : Message {
        return AttachmentHelper.addAttachmentMetadata(
            uploadedUri,
            message,
            context,
            audioUri,
            filePath,
            fileName
        )
    }

    fun isValidAudio(context: Context, audioUri: Uri) : Boolean {

        val mimeType = context.contentResolver.getType(audioUri)
        mimeType?.let {
            if (it.startsWith("audio/")) return true
            val numBytes = context.contentResolver.openInputStream(audioUri)?.use { stream ->
                stream.available()
            }
            numBytes?.let {
                return numBytes <= InstanceHandler.instance.localConfig.maxAudioUploadSize.coerceAtMost(MAX_AUDIO_SIZE)
            } ?: return false

        } ?: return false

    }

}