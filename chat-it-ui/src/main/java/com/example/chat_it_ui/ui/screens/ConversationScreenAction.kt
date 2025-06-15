package com.example.chat_it_ui.ui.screens

import android.content.Context
import android.net.Uri
import com.example.chat_it.model.MessageType
import com.example.chat_it_ui.ui.models.MessageUIModel

sealed interface ConversationScreenAction {
    data class uploadAttachment(val uri: Uri, val context: Context, val messageType: MessageType) : ConversationScreenAction
    data class OnSendButtonClicked(val messageText : String, val messageType: MessageType, val context: Context) : ConversationScreenAction
    data object onAttachmentRemoved : ConversationScreenAction
    data class OnPlayPauseButtonClicked(val messageUIModel: MessageUIModel) : ConversationScreenAction
}