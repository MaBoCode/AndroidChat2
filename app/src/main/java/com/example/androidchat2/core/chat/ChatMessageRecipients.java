package com.example.androidchat2.core.chat;

import java.io.Serializable;
import java.util.List;

public class ChatMessageRecipients implements Serializable {

    private List<String> messageIds;

    public ChatMessageRecipients() {
    }

    public ChatMessageRecipients(List<String> messageIds) {
        this.messageIds = messageIds;
    }

    public void addRecipient(String msgId) {
        messageIds.add(msgId);
    }

    public List<String> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(List<String> messageIds) {
        this.messageIds = messageIds;
    }

    @Override
    public String toString() {
        return "ChatMessageRecipients{" +
                "messageIds=" + messageIds +
                '}';
    }
}
