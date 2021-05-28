package com.example.androidchat2.core.chat;

import com.google.firebase.database.Exclude;
import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatDialog implements IDialog<ChatMessage>, Serializable {

    private String id;
    private String dialogName;
    private ChatMessage lastMessage = new ChatMessage(
            "welcome",
            "Start a new conversation with...",
            new ChatUser("welcome", "welcome"),
            null);

    private List<ChatUser> users = new ArrayList<>();

    public ChatDialog() {
    }

    public ChatDialog(String id, String dialogName, List<ChatUser> users) {
        this.id = id;
        this.dialogName = dialogName;
        this.users = users;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDialogName(String dialogName) {
        this.dialogName = dialogName;
    }

    public void setUsers(List<ChatUser> users) {
        this.users = users;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDialogPhoto() {
        return "https://cdn-images-1.listennotes.com/podcasts/star-wars-7x7-the-daily-star-wars-podcast-2LryqMj-sGP-AIg3cZVKCsL.300x300.jpg";
    }

    @Override
    public String getDialogName() {
        return dialogName;
    }

    @Override
    public List<? extends IUser> getUsers() {
        return users;
    }

    @Exclude
    @Override
    public ChatMessage getLastMessage() {
        return lastMessage;
    }

    @Exclude
    @Override
    public void setLastMessage(ChatMessage message) {
        this.lastMessage = message;
    }

    @Override
    public int getUnreadCount() {
        return 0;
    }

    @Override
    public String toString() {
        return "ChatDialog{" +
                "id='" + id + '\'' +
                ", dialogName='" + dialogName + '\'' +
                ", users=" + users +
                '}';
    }
}
