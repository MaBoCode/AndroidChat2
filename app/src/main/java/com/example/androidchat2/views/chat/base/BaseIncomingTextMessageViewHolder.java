package com.example.androidchat2.views.chat.base;

import android.view.View;

import com.example.androidchat2.R;
import com.example.androidchat2.core.chat.ChatMessage;
import com.stfalcon.chatkit.messages.MessageHolders;

public class BaseIncomingTextMessageViewHolder extends MessageHolders.IncomingTextMessageViewHolder<ChatMessage> {

    public BaseIncomingTextMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
    }

    @Override
    public void onBind(ChatMessage message) {
        bubble.setBackgroundResource(R.drawable.bg_chat_incoming_bubble_default);

        super.onBind(message);
    }
}
