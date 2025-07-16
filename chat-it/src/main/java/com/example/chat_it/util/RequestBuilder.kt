package com.example.chat_it.util

import com.example.chat_it.init.InstanceHandler
import com.example.chat_it.model.Message
import com.example.chat_it.model.MessageStatus
import com.example.chat_it.model.MessageType
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

fun JsonObjectBuilder.addMessageTextValue(message: Message) : JsonElement? {
    return put(
        TEXT,
        JsonPrimitive(
            message.message
        )
    )
}

fun JsonObjectBuilder.addAttachmentMessageMetadata(message: Message) : JsonElement? {
    return put(
        FILE_DATA,
        buildJsonObject {
            put(FILE_URI, JsonPrimitive(message.messageMetadata[FILE_URI]))
            put(MIME_TYPE, JsonPrimitive(message.messageMetadata[MIME_TYPE]))
        }
    )
}

fun JsonObjectBuilder.addQueryPrompt() : JsonElement? {
    return put(
        SYSTEM_INSTRUCTION,
        buildJsonObject {
            put(
                PARTS,
                buildJsonObject {
                    put(TEXT, JsonPrimitive(InstanceHandler.instance.localConfig.queryPrompt))
                }
            )
        }
    )
}

fun JsonObjectBuilder.addContents(messages: List<Message>) : JsonElement? {
    return put(
        CONTENTS,
        buildJsonArray {
            messages.forEach {
                if(isEligibleForAddingToRequestBody(it)){
                    addJsonObject {
                        addMessageSender(it)
                        addMessageParts(it)
                    }
                }
            }
        }
    )
}

fun JsonObjectBuilder.addMessageSender(message: Message) : JsonElement? {
    return put(ROLE, JsonPrimitive(if (message.isUserMessage()) USER else AI_MODEL))
}

fun JsonObjectBuilder.addMessageParts(message: Message) : JsonElement? {
    return put(PARTS,
        buildJsonArray {
            addJsonObject {
                addMessageTextValue(message)
            }
            if (!message.isTextMessage()) {
                addJsonObject {
                    addAttachmentMessageMetadata(message)
                }
            }
        }
    )
}

fun isEligibleForAddingToRequestBody(message: Message) = message.messageStatus != MessageStatus.FAILED && message.messageStatus != MessageStatus.QUEUED && message.message.isNotEmpty()