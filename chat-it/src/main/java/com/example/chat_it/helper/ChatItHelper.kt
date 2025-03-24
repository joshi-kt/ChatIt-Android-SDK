package com.example.chat_it.helper

import android.content.Context
import android.net.Uri
import androidx.annotation.WorkerThread
import com.example.chat_it.data.ChatServicesProvider
import com.example.chat_it.data.handler.AudioMessageHelper
import com.example.chat_it.data.local.ConversationCache
import com.example.chat_it.data.local.ConversationCache.messagesFlow
import com.example.chat_it.init.InstanceHandler
import com.example.chat_it.model.Message
import io.ktor.util.collections.ConcurrentSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object ChatItHelper {

    private val userChatHandler = ChatServicesProvider.userChatHandler
    private val runningJobs = ConcurrentSet<Job>()

    @JvmStatic
    suspend fun sendMessage(message: String) {
        InstanceHandler.ensureInitialized()
        userChatHandler.sendMessage(message)
    }

    @JvmStatic
    fun sendMessageAsync(
        message: String,
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ) {
        InstanceHandler.ensureInitialized()
        val job = coroutineScope.launch {
            userChatHandler.sendMessage(message)
        }
        runningJobs.add(job)
        job.invokeOnCompletion {
            runningJobs.remove(job)
        }
    }

    @JvmStatic
    fun sendAudioMessageAsync(
        audioUri : Uri,
        context : Context,
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ){
        InstanceHandler.ensureInitialized()
        val job = coroutineScope.launch {
            userChatHandler.sendAudioMessage(
                audioUri, context.applicationContext
            )
        }
        runningJobs.add(job)
        job.invokeOnCompletion {
            runningJobs.remove(job)
        }
    }

    @JvmStatic
    fun deleteMessage(messageId : String) {
        ConversationCache.deleteMessage(messageId)
    }

    @JvmStatic
    fun deleteAllMessages() {
        ConversationCache.clearMessages()
    }

    @JvmStatic
    suspend fun sendAudioMessage(
        audioUri : Uri,
        context : Context,
    ){
        InstanceHandler.ensureInitialized()
        userChatHandler.sendAudioMessage(
            audioUri, context.applicationContext
        )
    }

    @JvmStatic
    fun getAllMessages() : List<Message> {
        InstanceHandler.ensureInitialized()
        return ConversationCache.getMessages()
    }

    @JvmStatic
    fun getAllMessagesAsFlow() : Flow<List<Message>> {
        InstanceHandler.ensureInitialized()
        return ConversationCache.getAllMessagesAsFlow()
    }

    @JvmStatic
    fun addMessageListUpdateListener(listener: MessagesUpdateListener) {
        InstanceHandler.ensureInitialized()
        ConversationCache.addListener(listener)
    }

    @JvmStatic
    fun removeMessageListUpdateListener(listener: MessagesUpdateListener) {
        InstanceHandler.ensureInitialized()
        ConversationCache.removeListener(listener)
    }

    @JvmStatic
    internal fun clearOlderFiles(context: Context, onCleared : () -> Unit = {}) {
        AudioMessageHelper.clearOlderFiles(context, onCompleted = onCleared)
    }

    @JvmStatic
    fun stopChatIt() {
        runningJobs.forEach {
            it.cancel()
        }
        ConversationCache.clearMessages()
    }

}