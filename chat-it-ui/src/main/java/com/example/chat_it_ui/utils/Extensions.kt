package com.example.chat_it_ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.example.chat_it.model.Message
import com.example.chat_it.model.MessageSender
import com.example.chat_it.model.MessageStatus
import com.example.chat_it.model.MessageType
import com.example.chat_it_ui.ui.models.DisplayableDate
import com.example.chat_it_ui.ui.models.MessageUIModel
import java.util.UUID

fun Message.toMessageUIModel() = MessageUIModel(
    messageId = this.messageId,
    message = this.message,
    timeStamp = this.timeStamp.loggedTimeStamp.toDisplayableDate(),
    messageType = this.messageType,
    messageStatus = this.messageStatus,
    messageSender = this.messageSender,
    messageMetadata = this.messageMetadata
)

fun String.toMessageUIModel(
    messageType: MessageType,
    messageStatus: MessageStatus,
    messageSender: MessageSender
) = MessageUIModel(
    messageId = UUID.randomUUID().toString(),
    message = this,
    timeStamp = System.currentTimeMillis().toDisplayableDate(),
    messageType = messageType,
    messageStatus = messageStatus,
    messageSender = messageSender,
    messageMetadata = mapOf()
)

fun Long.toDisplayableDate() = DisplayableDate(this)

@Composable
fun Modifier.nonClickable() = composed {
    this.clickable(
        onClick = {},
        interactionSource = remember { MutableInteractionSource() },
        indication = null
    )
}

