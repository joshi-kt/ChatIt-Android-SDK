package com.example.chat_it.data.handler

import android.content.Context
import android.net.Uri
import androidx.annotation.WorkerThread
import com.example.chat_it.init.InstanceHandler
import com.example.chat_it.model.Message
import com.example.chat_it.util.MAX_IMAGE_SIZE

object ImageMessageHelper {

    private val IMAGE_FOLDER_NAME = "Chat It Images"

    @WorkerThread
    fun saveImageToDevice(
        imageUri: Uri,
        context: Context,
        message: Message
    ) : String {

        return AttachmentHelper.saveAttachmentToDevice(
            context,
            IMAGE_FOLDER_NAME,
            message,
            imageUri
        )

    }

    fun isValidImage(context: Context, audioUri: Uri) : Boolean {

        val mimeType = context.contentResolver.getType(audioUri)
        mimeType?.let {
            if (it.startsWith("image/")) return true
            val numBytes = context.contentResolver.openInputStream(audioUri)?.use { stream ->
                stream.available()
            }
            numBytes?.let {
                return numBytes <= InstanceHandler.instance.localConfig.maxImageUploadSize.coerceAtMost(
                    MAX_IMAGE_SIZE
                )
            } ?: return false

        } ?: return false

    }

    fun addImageMetadata(
        uploadedUri: String? = null,
        message: Message,
        context: Context,
        imageUri: Uri? = null,
        filePath : String? = null,
        fileName : String? = null
    ) : Message {
        return AttachmentHelper.addAttachmentMetadata(
            uploadedUri,
            message,
            context,
            imageUri,
            filePath,
        )
    }

}