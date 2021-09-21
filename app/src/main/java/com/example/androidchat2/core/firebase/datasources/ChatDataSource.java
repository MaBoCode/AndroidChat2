package com.example.androidchat2.core.firebase.datasources;

import android.util.Patterns;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import com.example.androidchat2.core.chat.ChatGroup;
import com.example.androidchat2.core.chat.ChatMessage;
import com.example.androidchat2.core.chat.ChatMessageRecipients;
import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.core.chat.ChatUserGroups;
import com.example.androidchat2.core.firebase.callbacks.ChatTaskCallback;
import com.example.androidchat2.core.firebase.databases.ChatRealTimeDatabase;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

public class ChatDataSource {

    protected ChatRealTimeDatabase chatDB;

    public interface ChatValueEventCallback<T> {
        void onChange(T payload);

        void onCancel(Exception e);
    }

    @Inject
    public ChatDataSource(ChatRealTimeDatabase chatDB) {
        this.chatDB = chatDB;
    }

    public void getGroups(ChatUserGroups userGroups, ChatTaskCallback<List<ChatGroup>> callback) {
        chatDB
                .getGroupsEndpoint()
                .get()
                .addOnSuccessListener(groupsSnapshot -> {
                    List<ChatGroup> groups = new ArrayList<>();

                    for (Map.Entry<String, Integer> entry : userGroups.getGroups().entrySet()) {
                        String groupId = entry.getKey();
                        int unreadCount = entry.getValue();

                        ChatGroup group = groupsSnapshot.child(groupId).getValue(ChatGroup.class);

                        if (group != null) {
                            group.setUnreadCount(unreadCount);
                            groups.add(group);
                        }
                    }

                    callback.onSuccess(groups);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getGroupById(String groupId, ChatTaskCallback<ChatGroup> callback) {
        chatDB
                .getGroupsEndpoint()
                .child(groupId)
                .get()
                .addOnSuccessListener(groupSnapshot -> {
                    ChatGroup group = groupSnapshot.getValue(ChatGroup.class);
                    callback.onSuccess(group);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getMessages(ChatMessageRecipients recipients, ChatTaskCallback<List<ChatMessage>> callback) {
        chatDB
                .getMessagesEndpoint()
                .get()
                .addOnSuccessListener(messagesSnapshot -> {
                    List<String> messagesIds = recipients.getMessageIds();
                    List<ChatMessage> chatMessages = new ArrayList<>();

                    for (String messageId : messagesIds) {
                        ChatMessage chatMessage = messagesSnapshot
                                .child(messageId)
                                .getValue(ChatMessage.class);
                        chatMessages.add(chatMessage);
                    }
                    callback.onSuccess(chatMessages);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public Task<Void> setGroupUnreadCount(String userId, String groupId, int unreadCount) {
        return chatDB
                .getUserGroupsEndpoint()
                .child(userId)
                .child(groupId)
                .setValue(unreadCount);
    }

    public Task<Void> setMessageRecipients(String groupId, ChatMessageRecipients recipients) {
        return chatDB
                .getMessageRecipientsEndpoint()
                .child(groupId)
                .setValue(recipients);
    }

    public void isUserCurrentlyLooking(String groupId, String userId, ChatTaskCallback<Boolean> callback) {
        chatDB
                .getGroupsEndpoint()
                .child(groupId)
                .child("isLooking")
                .child(userId)
                .get()
                .addOnSuccessListener(isLookingSnapshot -> {
                    Boolean isLooking = (Boolean) isLookingSnapshot.getValue();

                    if (isLooking != null) {
                        callback.onSuccess(isLooking);
                    } else {
                        callback.onFailure(null);
                    }

                })
                .addOnFailureListener(callback::onFailure);
    }

    public Task<Void> setIsUserCurrentlyLooking(String groupId, String userId, boolean isLooking) {
        return chatDB
                .getGroupsEndpoint()
                .child(groupId)
                .child("isLooking")
                .child(userId)
                .setValue(isLooking);
    }

    public Task<Void> setGroupLastMessage(String groupId, ChatMessage lastMessage) {
        return chatDB
                .getGroupsEndpoint()
                .child(groupId)
                .child("lastMsg")
                .setValue(lastMessage);
    }

    public Task<Void> addMessage(ChatMessage message) {
        return chatDB
                .insertValue(chatDB.getMessagesEndpoint(), message.getId(), message);
    }

    public void listenForMessagesUpdates(String groupId, ChatValueEventCallback<ChatMessageRecipients> callback) {
        chatDB
                .getMessageRecipientsEndpoint()
                .child(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ChatMessageRecipients recipients = snapshot.getValue(ChatMessageRecipients.class);

                        if (recipients != null) {
                            callback.onChange(recipients);
                        } else {
                            callback.onCancel(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onCancel(error.toException());
                    }
                });
    }

    public void listenForGroupCreation(String userId, ChatValueEventCallback<ChatUserGroups> callback) {
        chatDB
                .getUserGroupsEndpoint()
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Map<String, Integer> groupsMap = new HashMap<>();

                        for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                            String groupId = groupSnapshot.getKey();
                            Long unreadCount = (Long) groupSnapshot.getValue();
                            groupsMap.put(groupId, unreadCount.intValue());
                        }

                        ChatUserGroups userGroups = new ChatUserGroups(groupsMap);
                        callback.onChange(userGroups);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onCancel(error.toException());
                    }
                });
    }

    public void listenForGroupsUpdates(ChatUserGroups userGroups, ChatValueEventCallback<ChatGroup> callback) {
        for (Map.Entry<String, Integer> entry : userGroups.getGroups().entrySet()) {
            listenForGroupUpdates(entry.getKey(), entry.getValue(), callback);
        }
    }

    public void listenForGroupUpdates(String groupId, int unreadCount, ChatValueEventCallback<ChatGroup> callback) {
        chatDB
                .getGroupsEndpoint()
                .child(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ChatGroup group = snapshot.getValue(ChatGroup.class);

                        if (group != null) {
                            group.setUnreadCount(unreadCount);
                            callback.onChange(group);
                        } else {
                            callback.onCancel(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onCancel(error.toException());
                    }
                });
    }

    public ChatMessage buildMessage(String groupId, String content, ChatUser user) {
        String msgId = chatDB.getNewKey(chatDB.getMessagesEndpoint());

        Date currentDate = new Date();
        ChatMessage msg = new ChatMessage(msgId, content, user, currentDate, groupId, null);

        if (Patterns.WEB_URL.matcher(content).matches()) {
            Set<String> imageExtensions = new HashSet<>();
            imageExtensions.add("gif");
            imageExtensions.add("jpg");
            imageExtensions.add("jpeg");
            imageExtensions.add("png");

            String extension = MimeTypeMap.getFileExtensionFromUrl(content);

            if (imageExtensions.contains(extension)) {
                msg.setText(null);
                msg.setImageUrl(content);
            }
        }
        return msg;
    }

    public void sendMessage(ChatMessage message, ChatUser user, ChatGroup group, ChatMessageRecipients recipients) {
        setMessageRecipients(group.getId(), recipients);

        updateUsersUnreadCount(user.getId(), group.getId(), group.getUserIds(), new ChatTaskCallback<Void>() {
            @Override
            public void onSuccess(Void payload) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        addMessage(message)
                .addOnSuccessListener(unused -> {
                    if (message.getImageUrl() != null) {
                        String text = String.format("%s sent an image", user.getName());
                        message.setText(text);
                    }

                    setGroupLastMessage(group.getId(), message);
                });
    }

    public void updateUsersUnreadCount(String currentUserId, String groupId, List<String> userIds, ChatTaskCallback<Void> callback) {
        chatDB
                .getUserGroupsEndpoint()
                .get()
                .addOnSuccessListener(userGroupsSnapshot -> {
                    for (String userId : userIds) {

                        if (userId.contentEquals(currentUserId)) {
                            continue;
                        }

                        updateUserUnreadCount(groupId, userId, userGroupsSnapshot, callback);
                    }
                })
                .addOnFailureListener(callback::onFailure);

    }

    public void updateUserUnreadCount(String groupId, String userId, DataSnapshot userGroupsSnapshot, ChatTaskCallback<Void> callback) {
        isUserCurrentlyLooking(groupId, userId, new ChatTaskCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean isLooking) {
                if (!isLooking) {
                    DataSnapshot userSnapshot = userGroupsSnapshot.child(userId);
                    Long unreadCount = (Long) userSnapshot.child(groupId).getValue();

                    if (unreadCount == null) {
                        callback.onFailure(new RuntimeException("UnreadCount is null"));
                    } else {
                        chatDB
                                .updateValue(userSnapshot.getRef(), groupId, unreadCount + 1)
                                .addOnFailureListener(callback::onFailure);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public ChatGroup buildGroup(String groupName, List<ChatUser> users) {
        String groupId = chatDB.getNewKey(chatDB.getGroupsEndpoint());

        return new ChatGroup(groupId, groupName, users);
    }
}
