package com.example.chat_it_ui.helper

import android.content.Context
import android.content.Intent
import com.example.chat_it_ui.ui.activities.ChatActivity
import com.example.chat_it_ui.utils.ModuleNotFoundException
import com.example.chat_it_ui.utils.Utils

object ChatItUIHelper {

    fun startChat(context : Context) {
        if (Utils.isClassAvailable("com.example.chat_it.helper.ChatItHelper")) {
            val intent = Intent(context, ChatActivity::class.java)
            context.startActivity(intent)
        } else {
            throw ModuleNotFoundException("chat-it-core")
        }
    }

}