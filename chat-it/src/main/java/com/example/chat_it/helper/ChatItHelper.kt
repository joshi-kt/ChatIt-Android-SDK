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
import com.example.chat_it.util.SDKUninitializedException
import io.ktor.util.collections.ConcurrentSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.Throws

object ChatItHelper {

    private val userChatHandler = ChatServicesProvider.userChatHandler
    private val runningJobs = ConcurrentSet<Job>()

    @JvmStatic
    @Throws(SDKUninitializedException::class)
    suspend fun sendMessage(message: String) {
        InstanceHandler.ensureInitialized()
        userChatHandler.sendMessage(message)
    }

    @JvmOverloads
    @JvmStatic
    @Throws(SDKUninitializedException::class)
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

    @JvmOverloads
    @JvmStatic
    @Throws(SDKUninitializedException::class)
    fun sendAudioMessageAsync(
        messageText: String,
        audioUri : Uri,
        context : Context,
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ){
        InstanceHandler.ensureInitialized()
        val job = coroutineScope.launch {
            userChatHandler.sendAudioMessage(
                audioUri,
                context.applicationContext,
                messageText
            )
        }
        runningJobs.add(job)
        job.invokeOnCompletion {
            runningJobs.remove(job)
        }
    }

    @JvmOverloads
    @JvmStatic
    @Throws(SDKUninitializedException::class)
    fun sendImageMessageAsync(
        messageText: String,
        imageUri: Uri,
        context : Context,
        coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ){
        InstanceHandler.ensureInitialized()
        val job = coroutineScope.launch {
            userChatHandler.sendImageMessage(
                imageUri,
                context.applicationContext,
                messageText
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
    @Throws(SDKUninitializedException::class)
    suspend fun sendAudioMessage(
        audioUri : Uri,
        context : Context,
        messageText : String,
    ){
        InstanceHandler.ensureInitialized()
        userChatHandler.sendAudioMessage(
            audioUri,
            context.applicationContext,
            messageText
        )
    }

    @JvmStatic
    @Throws(SDKUninitializedException::class)
    suspend fun sendImageMessage(
        imageUri : Uri,
        context : Context,
        messageText : String,
    ){
        InstanceHandler.ensureInitialized()
        userChatHandler.sendImageMessage(
            imageUri,
            context.applicationContext,
            messageText
        )
    }

    @JvmStatic
    @Throws(SDKUninitializedException::class)
    fun getAllMessages() : List<Message> {
        InstanceHandler.ensureInitialized()
        return ConversationCache.getMessages()
    }

    @JvmStatic
    @Throws(SDKUninitializedException::class)
    fun getAllMessagesAsFlow() : Flow<List<Message>> {
        InstanceHandler.ensureInitialized()
        return ConversationCache.getAllMessagesAsFlow()
    }

    @JvmStatic
    @Throws(SDKUninitializedException::class)
    fun addMessageListUpdateListener(listener: MessagesUpdateListener) {
        InstanceHandler.ensureInitialized()
        ConversationCache.addListener(listener)
    }

    @JvmStatic
    @Throws(SDKUninitializedException::class)
    fun removeMessageListUpdateListener(listener: MessagesUpdateListener) {
        InstanceHandler.ensureInitialized()
        ConversationCache.removeListener(listener)
    }

    @JvmStatic
    fun stopChatIt() {
        runningJobs.forEach {
            it.cancel()
        }
        ConversationCache.clearMessages()
    }

}