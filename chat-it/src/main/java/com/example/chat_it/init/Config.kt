package com.example.chat_it.init

import androidx.annotation.DrawableRes
import com.example.chat_it.util.DEFAULT_AUDIO_MESSAGE_OUTPUT_INSTRUCTION
import com.example.chat_it.util.DEFAULT_BOT_NAME
import com.example.chat_it.util.DEFAULT_IMAGE
import com.example.chat_it.util.DEFAULT_PROMPT
import com.example.chat_it.util.MAX_AUDIO_SIZE
import com.example.chat_it.util.MAX_IMAGE_SIZE
import kotlinx.serialization.Serializable

@Serializable
data class LocalConfig @JvmOverloads constructor(
    val queryPrompt : String = DEFAULT_PROMPT,
    @DrawableRes val botImage : Int = DEFAULT_IMAGE,
    val botName : String = DEFAULT_BOT_NAME,
    val botAudioQueryPrompt : String = DEFAULT_AUDIO_MESSAGE_OUTPUT_INSTRUCTION,
    val maxAudioUploadSize : Int = MAX_AUDIO_SIZE,
    val maxImageUploadSize : Int = MAX_IMAGE_SIZE
)

@Serializable
data class LogConfig @JvmOverloads constructor(
    val onDebugBuild : Boolean = true,
    val onReleaseBuild : Boolean = false
)