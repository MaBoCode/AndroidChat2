package com.example.androidchat2.core.chat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatUserGroups implements Serializable {

    private Map<String, Integer> groups;

    public ChatUserGroups() {
    }

    public ChatUserGroups(List<String> groupIds) {
        this.groups = new HashMap<>();
        for (String groupId : groupIds) {
            this.groups.put(groupId, 0);
        }
    }

    public ChatUserGroups(Map<String, Integer> groups) {
        this.groups = groups;
    }

    public void addGroup(String groupId) {
        this.groups.put(groupId, 0);
    }

    public Map<String, Integer> getGroups() {
        return groups;
    }
}
