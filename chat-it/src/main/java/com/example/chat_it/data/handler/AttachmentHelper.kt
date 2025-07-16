package com.example.chat_it.data.handler

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.annotation.WorkerThread
import com.example.chat_it.model.Message
import com.example.chat_it.util.FILE_NAME
import com.example.chat_it.util.FILE_URI
import com.example.chat_it.util.LOCAL_FILE_PATH
import com.example.chat_it.util.Logger
import com.example.chat_it.util.MIME_TYPE
import java.io.File
import java.io.FileOutputStream

object AttachmentHelper {

    private val TAG = "AttachmentHelper"

    @WorkerThread
    fun saveAttachmentToDevice(
        context : Context,
        folderName : String,
        message: Message,
        fileUri: Uri
    ) : String {

        val contentResolver = context.contentResolver
        val audioFolder = File(context.cacheDir, folderName).apply {
            if (!exists()) mkdirs()
        }

        val localFile = File(audioFolder, message.messageId)

        contentResolver.openInputStream(fileUri)?.use { inputStream ->
            FileOutputStream(localFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        Logger.log(TAG, Logger.LogType.DEBUG, "Attachment saved to local storage: ${localFile.path}")

        return localFile.path

    }

    @WorkerThread
    fun getFileNameFromUri(context: Context, fileUri: Uri): String {
        var fileName = ""
        context.contentResolver.query(fileUri, null, null, null, null)?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                fileName = it.getString(nameIndex)
            }
        }

        return fileName.ifBlank { fileUri.lastPathSegment.toString() }
    }

    fun addAttachmentMetadata(
        uploadedUri: String? = null,
        message: Message,
        context: Context,
        fileUri: Uri? = null,
        filePath : String? = null,
        fileName : String? = null
    ) : Message {
        val metadata = message.messageMetadata.toMutableMap()
        uploadedUri?.let {
            metadata[FILE_URI] = it
        }
        fileUri?.let {
            metadata[MIME_TYPE] = context.contentResolver.getType(it).toString()
        }
        filePath?.let {
            metadata[LOCAL_FILE_PATH] = it
        }
        fileName?.let {
            metadata[FILE_NAME] = it
        }
        return message.copy(
            messageMetadata = metadata.toMap()
        )
    }

}