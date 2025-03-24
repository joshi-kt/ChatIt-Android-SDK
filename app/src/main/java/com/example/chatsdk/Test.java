package com.example.chatsdk;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.chat_it.helper.ChatItHelper;
import com.example.chat_it.helper.MessagesUpdateListener;
import com.example.chat_it.model.Message;
import com.example.chat_it_ui.helper.ChatItUIHelper;

import java.util.List;

public class Test {

    static void startChat(Context context) {
        ChatItHelper.addMessageListUpdateListener(messages -> {

        });
    }

}
