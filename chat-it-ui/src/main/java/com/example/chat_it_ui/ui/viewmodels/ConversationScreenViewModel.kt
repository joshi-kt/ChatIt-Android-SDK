package com.example.chat_it_ui.ui.viewmodels

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_it.helper.ChatItHelper
import com.example.chat_it.model.Message
import com.example.chat_it.model.MessageType
import com.example.chat_it.util.Logger
import com.example.chat_it_ui.media.MediaHandler
import com.example.chat_it_ui.ui.models.MessageUIModel
import com.example.chat_it_ui.ui.screens.ConversationScreenAction
import com.example.chat_it_ui.ui.screens.ConversationUIState
import com.example.chat_it_ui.utils.ConnectivityObserver
import com.example.chat_it_ui.utils.toMessageUIModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import androidx.core.net.toUri

internal class ConversationScreenViewModel : ViewModel() {

    private val TAG = "ConversationScreenViewModel"

    private val mutex = Mutex()
    private val chatMessages = ChatItHelper.getAllMessagesAsFlow()

    private val _uiState = MutableStateFlow(ConversationUIState())
    val uiState = _uiState.asStateFlow()

    private val mediaHandler = MediaHandler()

    init {
        observeMessages()
        observeAudioFinishing()
    }

    private fun observeAudioFinishing() {
        mediaHandler.onAudioComplete {
            updateCurrentPlayingUIState()
        }
    }

    fun startObservingInternet(context: Context) {
        executeAsync(
            onBackgroundThread = false,
            action = {
                ConnectivityObserver.observeConnectivity(context).collectLatest {
                    if (it != _uiState.value.internetConnectivity) {
                        updateConnectivityState(it)
                    }
                }
            }
        )
    }

    private fun updateConnectivityState(state : Boolean) {
        _uiState.update {
            it.copy(
                internetConnectivity = state
            )
        }
    }

    fun onAction(action: ConversationScreenAction){
        when(action){

            is ConversationScreenAction.OnSendButtonClicked -> {

                when(action.messageType) {
                    MessageType.TEXT -> {
                        sendTextMessage(action.messageText)
                    }
                    MessageType.AUDIO -> {
                        sendAudioMessage(_uiState.value.uploadedFileUri, action.messageText, action.context)
                    }
                }
                resetMessageUIState()

            }

            is ConversationScreenAction.OnPlayPauseButtonClicked -> {

                if (mediaHandler.isPlaying()) {
                    mediaHandler.pause()
                    updateCurrentPlayingUIState()
                } else {
                    mediaHandler.play(
                        messageUIModel = action.messageUIModel,
                        onStarted = {
                            updateCurrentPlayingUIState(messageId = action.messageUIModel.messageId)
                        }
                    )
                }

            }

            ConversationScreenAction.onAttachmentRemoved -> {

                resetMessageUIState()

            }

            is ConversationScreenAction.uploadAttachment -> {
                updateUploadingUIState(true)
                uploadAttachment(action)
            }

        }
    }

    private fun resetMessageUIState() {
        _uiState.update {
            it.copy(
                uploadedFileName = "",
                uploadedFileUri = "",
                messageType = MessageType.TEXT
            )
        }
    }

    private fun updateUploadingUIState(isUploading : Boolean) {
        _uiState.update {
            it.copy(
                isUploading = isUploading,
            )
        }
    }

    private fun uploadAttachment(action: ConversationScreenAction.uploadAttachment) {
        executeAsync(
            isSynchronized = true,
            action = {
                val fileName = getFileNameFromUri(context = action.context, uri = action.uri)
                updateCurrentMessageTypeUI(
                    uploadedFileUri = action.uri.toString(),
                    uploadedFileName = fileName,
                    messageType = action.messageType
                )
                updateUploadingUIState(false)
            }
        )
    }

    private fun updateCurrentMessageTypeUI(
        uploadedFileUri : String,
        uploadedFileName : String,
        messageType: MessageType
    ) {
        _uiState.update {
            it.copy(
                uploadedFileName = uploadedFileName,
                uploadedFileUri = uploadedFileUri,
                messageType = messageType
            )
        }
    }

    private fun updateCurrentPlayingUIState(messageId: String? = null) {
        _uiState.update {
            it.copy(
                currentPlaying = messageId
            )
        }
    }

    private fun sendTextMessage(messageText : String){
        executeAsync (
            action = {
                ChatItHelper.sendMessage(messageText)
            }
        )
    }

    private fun sendAudioMessage(
        audioUriString: String,
        messageText: String,
        context: Context
    ){
        executeAsync (
            action = {
                val audioUri = audioUriString.toUri()
                ChatItHelper.sendAudioMessage(
                    audioUri = audioUri,
                    context = context,
                    messageText = messageText
                )
            }
        )
    }

    private fun observeMessages() {
        executeAsync(
            action = {
                chatMessages.collectLatest { messages ->
                    Logger.log(TAG, Logger.LogType.DEBUG, messages.toString())
                    _uiState.update {
                        it.copy (
                            messages = messages.map { message -> message.toMessageUIModel() }
                        )
                    }
                }
            }
        )
    }

    private fun executeAsync(
        isSynchronized: Boolean = false,
        onBackgroundThread : Boolean = true,
        action: suspend () -> Unit,
        onError: (Throwable) -> Unit = { _ -> }
    ): Job {

        val handler = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
            onError(throwable)
        }

        val dispatcher = if (onBackgroundThread) Dispatchers.IO else Dispatchers.Main

        return viewModelScope.launch(dispatcher + handler) {
            if (isSynchronized) {
                mutex.withLock {
                    action()
                }
            } else {
                action()
            }
        }
    }

    private suspend fun withMainContext(block: suspend () -> Unit) {
        withContext(Dispatchers.Main) {
            block()
        }
    }

    private suspend fun withIOContext(block: suspend () -> Unit) {
        withContext(Dispatchers.IO) {
            block()
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaHandler.release()
    }

    @WorkerThread
    fun getFileNameFromUri(context: Context, uri: Uri): String {
        var fileName = ""
        context.contentResolver.query(uri, null, null, null, null)?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                fileName = it.getString(nameIndex)
            }
        }

        return fileName.ifBlank { uri.lastPathSegment.toString() }
    }

}