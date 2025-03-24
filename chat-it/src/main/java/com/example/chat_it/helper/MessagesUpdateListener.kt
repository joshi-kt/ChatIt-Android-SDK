package com.example.chat_it.helper

import com.example.chat_it.model.Message
import java.util.UUID

interface MessagesUpdateListener {

    fun onMessagesUpdated(messages: List<Message>)

}