package com.example.androidchat2.core.chat;

import java.io.Serializable;

public class ChatMessageRecipient implements Serializable {

    private String id;
    private String recipientId;
    private String recipientGroupId;
    private String messageId;

    public ChatMessageRecipient() {
    }

    public ChatMessageRecipient(String id, String recipientId, String recipientGroupId, String messageId) {
        this.id = id;
        this.recipientId = recipientId;
        this.recipientGroupId = recipientGroupId;
        this.messageId = messageId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientGroupId() {
        return recipientGroupId;
    }

    public void setRecipientGroupId(String recipientGroupId) {
        this.recipientGroupId = recipientGroupId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
