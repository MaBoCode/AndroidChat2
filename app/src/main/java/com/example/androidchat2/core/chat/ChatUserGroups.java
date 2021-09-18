package com.example.androidchat2.core.chat;

import java.io.Serializable;
import java.util.List;

public class ChatUserGroups implements Serializable {

    private List<String> groupIds;

    public ChatUserGroups() {
    }

    public ChatUserGroups(List<String> groupIds) {
        this.groupIds = groupIds;
    }

    public void addGroup(String groupId) {
        groupIds.add(groupId);
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }

    @Override
    public String toString() {
        return "ChatUserGroup{" +
                "groupIds=" + groupIds +
                '}';
    }
}
