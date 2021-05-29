package com.example.androidchat2.core.chat;

import java.io.Serializable;

public class ChatUserGroup implements Serializable {

    private String id;
    private String userId;
    private String groupId;

    public ChatUserGroup() {
    }

    public ChatUserGroup(String id, String userId, String groupId) {
        this.id = id;
        this.userId = userId;
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "ChatUserGroup{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", groupId='" + groupId + '\'' +
                '}';
    }
}
