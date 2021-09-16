package com.example.androidchat2.core.chat;

import com.google.firebase.auth.FirebaseUser;
import com.stfalcon.chatkit.commons.models.IUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatUser implements IUser, Serializable {

    private String id;
    private String name;
    private String email;
    private List<String> dialogIds = new ArrayList<>();
    private Set<String> notificationTokens = new HashSet<>();

    public ChatUser() {
    }

    public ChatUser(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public ChatUser(FirebaseUser firebaseUser) {
        this.id = firebaseUser.getUid();
        this.name = firebaseUser.getDisplayName();
        this.email = firebaseUser.getEmail();
    }

    public boolean addDialogId(String dialogId) {
        return dialogIds.add(dialogId);
    }

    public boolean addNotificationToken(String token) {
        return notificationTokens.add(token);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNotificationTokens(List<String> notificationTokens) {
        this.notificationTokens = new HashSet<>(notificationTokens);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getAvatar() {
        return "https://cdn-images-1.listennotes.com/podcasts/star-wars-7x7-the-daily-star-wars-podcast-2LryqMj-sGP-AIg3cZVKCsL.300x300.jpg";
    }

    public List<String> getDialogIds() {
        return dialogIds;
    }

    public List<String> getNotificationTokens() {
        return new ArrayList<>(notificationTokens);
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
