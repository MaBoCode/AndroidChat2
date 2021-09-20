package com.example.androidchat2.core.chat;

import androidx.annotation.Nullable;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage implements IMessage, MessageContentType.Image, Serializable {

    private String id;
    private String text;
    private ChatUser user;
    private Date createdAt;
    private String toGroupId;
    private String imageUrl;

    public ChatMessage() {
    }

    public ChatMessage(String id, String text, ChatUser user, Date createdAt, String toGroupId, String imageUrl) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.createdAt = createdAt;
        this.toGroupId = toGroupId;
        this.imageUrl = imageUrl;
    }

    public ChatMessage(ChatMessage message) {
        this.id = message.getId();
        this.text = message.getText();
        this.user = (ChatUser) message.getUser();
        this.createdAt = message.getCreatedAt();
        this.toGroupId = message.getToGroupId();
        this.imageUrl = message.getImageUrl();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    // TODO: exclude, fetch user by id
    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    public String getToGroupId() {
        return toGroupId;
    }

    @Nullable
    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    // TODO: exclude, fetch user by id
    public void setUser(ChatUser user) {
        this.user = user;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setToGroupId(String toGroupId) {
        this.toGroupId = toGroupId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", user=" + user +
                ", createdAt=" + createdAt +
                ", toGroupId='" + toGroupId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
