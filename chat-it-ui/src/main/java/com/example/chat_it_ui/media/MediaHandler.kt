package com.example.chat_it_ui.media

import android.media.MediaPlayer
import com.example.chat_it_ui.ui.models.MessageUIModel

class MediaHandler {

    private val mediaPlayer = MediaPlayer()
    private var currentPlayingMessage : MessageUIModel? = null

    private fun prepare(
        filePath : String,
        onReady : () -> Unit
    ) {
        mediaPlayer.apply {
            setDataSource(filePath)
            setOnPreparedListener {
                onReady()
            }
            prepareAsync()
        }
    }

    fun play(
        onStarted: () -> Unit,
        messageUIModel: MessageUIModel,
        onError: (Throwable) -> Unit = {}
    ) {

        try {

            if(isCurrentPlaying(messageUIModel)) {
                mediaPlayer.start()
                onStarted()
                return
            }

            val filePath = messageUIModel.getAudioLocalPath()
            checkNotNull(filePath) { "Invalid Audio Path" }
            prepare(
                filePath = filePath,
                onReady = {
                    mediaPlayer.start()
                    updateCurrentPlaying(messageUIModel)
                    onStarted()
                }
            )

        } catch (e : Exception){
            e.printStackTrace()
            onError(e)
        }
    }

    private fun updateCurrentPlaying(messageUIModel: MessageUIModel) {
        currentPlayingMessage = messageUIModel
    }

    private fun isCurrentPlaying(messageUIModel: MessageUIModel) : Boolean {
        return currentPlayingMessage?.messageId == messageUIModel.messageId
    }

    fun isPlaying(): Boolean = runCatching { mediaPlayer.isPlaying }.onFailure{ it.printStackTrace() }.getOrDefault(false)

    fun currentQueuedAudio() : MessageUIModel? = currentPlayingMessage

    fun onAudioComplete(block: () -> Unit) {
        mediaPlayer.setOnCompletionListener {
            block()
        }
    }

    fun pause() = runCatching { mediaPlayer.pause() }.onFailure { it.printStackTrace() }

    fun release() {
        mediaPlayer.release()
    }

}