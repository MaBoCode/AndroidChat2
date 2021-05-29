package com.example.androidchat2.core.chat;

import com.google.firebase.auth.FirebaseUser;
import com.stfalcon.chatkit.commons.models.IUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatUser implements IUser, Serializable {

    private String id;
    private String name;
    private List<String> dialogIds = new ArrayList<>();

    public ChatUser() {
    }

    public ChatUser(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public ChatUser(FirebaseUser firebaseUser) {
        this.id = firebaseUser.getUid();
        this.name = firebaseUser.getEmail();
    }

    public boolean addDialogId(String dialogId) {
        return dialogIds.add(dialogId);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return "https://cdn-images-1.listennotes.com/podcasts/star-wars-7x7-the-daily-star-wars-podcast-2LryqMj-sGP-AIg3cZVKCsL.300x300.jpg";
    }

    public List<String> getDialogIds() {
        return dialogIds;
    }

    @Override
    public String toString() {
        return "ChatUser{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", dialogIds=" + dialogIds +
                '}';
    }
}
