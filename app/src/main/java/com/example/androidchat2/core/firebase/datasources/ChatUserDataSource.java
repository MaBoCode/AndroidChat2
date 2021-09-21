package com.example.androidchat2.core.firebase.datasources;

import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.core.chat.ChatUserGroups;
import com.example.androidchat2.core.firebase.callbacks.ChatTaskCallback;
import com.example.androidchat2.core.firebase.databases.ChatRealTimeDatabase;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ChatUserDataSource {

    protected ChatRealTimeDatabase chatDB;

    @Inject
    public ChatUserDataSource(ChatRealTimeDatabase chatDB) {
        this.chatDB = chatDB;
    }

    public void getUsersFromIds(List<String> userIds, ChatTaskCallback<List<ChatUser>> callback) {
        chatDB
                .getUsersEndpoint()
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ChatUser> users = new ArrayList<>();

                    for (String userId : userIds) {
                        ChatUser user = snapshot.child(userId).getValue(ChatUser.class);
                        users.add(user);
                    }

                    if (users.isEmpty()) {
                        callback.onFailure(new RuntimeException("Users list is empty."));
                    } else {
                        callback.onSuccess(users);
                    }

                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getUsers(ChatTaskCallback<List<ChatUser>> callback) {
        chatDB.getUsersEndpoint()
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ChatUser> users = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ChatUser user = dataSnapshot.getValue(ChatUser.class);
                        users.add(user);
                    }
                    callback.onSuccess(users);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getUserGroups(String userId, ChatTaskCallback<ChatUserGroups> callback) {
        chatDB
                .getUserGroupsEndpoint()
                .child(userId)
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    ChatUserGroups userGroups = dataSnapshot.getValue(ChatUserGroups.class);
                    callback.onSuccess(userGroups);
                })
                .addOnFailureListener(callback::onFailure);
    }

}
