package com.example.androidchat2.core.chat;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage implements IMessage, Serializable {

    private String id;
    private String text;
    private ChatUser user;
    private Date createdAt;

    public ChatMessage() {
    }

    public ChatMessage(String id, String text, ChatUser user, Date createdAt) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUser(ChatUser user) {
        this.user = user;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", user=" + user +
                ", createdAt=" + createdAt +
                '}';
    }
}
