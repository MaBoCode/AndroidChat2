package com.example.androidchat2.views.chat.viewmodels;

import android.content.Context;
import android.util.Patterns;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidchat2.core.chat.ChatGroup;
import com.example.androidchat2.core.chat.ChatMessage;
import com.example.androidchat2.core.chat.ChatMessageRecipients;
import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.core.firebase.ChatRealTimeDatabase;
import com.example.androidchat2.injects.base.BaseViewModel;
import com.example.androidchat2.utils.Logs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatMessagesFragmentViewModel extends BaseViewModel {

    @Inject
    protected ChatRealTimeDatabase chatDB;

    @Inject
    protected FirebaseMessaging firebaseMessaging;

    protected ChatMessageRecipients recipients = null;

    protected MutableLiveData<ChatGroup> _currentGroupLiveData = new MutableLiveData<>();
    public LiveData<ChatGroup> currentGroupLiveData = _currentGroupLiveData;

    protected MutableLiveData<List<ChatUser>> _groupChatUsers = new MutableLiveData<>();
    public LiveData<List<ChatUser>> groupChatUsers = _groupChatUsers;

    protected MutableLiveData<List<ChatMessage>> _messagesLiveData = new MutableLiveData<>();
    public LiveData<List<ChatMessage>> messagesLiveData = _messagesLiveData;

    @Inject
    public ChatMessagesFragmentViewModel(SavedStateHandle savedStateHandle) {
        super(savedStateHandle);
    }

    public void sendMessage(ChatUser currentChatUser, ChatGroup currentGroup, String value) {
        String msgId = chatDB.getNewKey(chatDB.getMessagesEndpoint());

        Date currentDate = new Date();
        String currentGroupId = currentGroup.getId();
        ChatMessage msg = new ChatMessage(msgId, value, currentChatUser, currentDate, currentGroupId, null);

        if (Patterns.WEB_URL.matcher(value).matches()) {
            Set<String> imageExtensions = new HashSet<>();
            imageExtensions.add("gif");
            imageExtensions.add("jpg");
            imageExtensions.add("jpeg");
            imageExtensions.add("png");

            String extension = MimeTypeMap.getFileExtensionFromUrl(value);

            if (imageExtensions.contains(extension)) {
                msg.setText(null);
                msg.setImageUrl(value);
            }
        }

        if (recipients != null) {
            recipients.addRecipient(msgId);
        } else {
            this.recipients = new ChatMessageRecipients(Arrays.asList(msgId));
        }

        chatDB
                .getMessageRecipientsEndpoint()
                .child(currentGroupId)
                .setValue(recipients);

        chatDB
                .getUserGroupsEndpoint()
                .get()
                .addOnSuccessListener(userGroupsSnapshot -> {
                    for (String userId : currentGroup.getUserIds()) {

                        if (userId.contentEquals(currentChatUser.getId())) {
                            continue;
                        }

                        isUserLooking(currentGroup, userId)
                                .addOnSuccessListener(isLookingSnapshot -> {
                                    Boolean isLooking = (Boolean) isLookingSnapshot.getValue();

                                    if (isLooking == null) {
                                        throw new RuntimeException("isLooking is null");
                                    }

                                    if (!isLooking) {
                                        DataSnapshot userSnapshot = userGroupsSnapshot.child(userId);
                                        Long unreadCount = (Long) userSnapshot.child(currentGroupId).getValue();

                                        if (unreadCount == null) {
                                            throw new RuntimeException("UnreadCount is null");
                                        }

                                        chatDB
                                                .updateValue(userSnapshot.getRef(), currentGroupId, unreadCount + 1);
                                    }
                                });

                    }
                });

        chatDB
                .insertValue(chatDB.getMessagesEndpoint(), msg.getId(), msg)
                .addOnSuccessListener(unused -> {

                    if (msg.getImageUrl() != null) {
                        String text = String.format("%s sent an image", currentChatUser.getName());
                        msg.setText(text);
                    }

                    setDialogLastMessage(currentGroup, msg);
                });
    }

    public Task<DataSnapshot> isUserLooking(ChatGroup currentGroup, String userId) {
        return chatDB
                .getGroupsEndpoint()
                .child(currentGroup.getId())
                .child("isLooking")
                .child(userId)
                .get();
    }

    public void getGroupById(String groupId) {
        chatDB
                .getGroupsEndpoint()
                .child(groupId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    ChatGroup group = snapshot.getValue(ChatGroup.class);

                    getUsersFromIds(group.getUserIds());

                    _currentGroupLiveData.postValue(group);
                });
    }

    public void getUsersFromIds(List<String> userIds) {
        chatDB
                .getUsersEndpoint()
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ChatUser> groupUsers = new ArrayList<>();

                    for (String userId : userIds) {
                        ChatUser chatUser = snapshot.child(userId).getValue(ChatUser.class);
                        groupUsers.add(chatUser);
                    }

                    if (groupUsers.isEmpty()) {
                        throw new RuntimeException("Users is empty");
                    }

                    _groupChatUsers.postValue(groupUsers);
                });
    }

    public void getMessages(ChatMessageRecipients chatMessageRecipients) {
        chatDB
                .getMessagesEndpoint()
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<String> messagesIds = chatMessageRecipients.getMessageIds();
                    List<ChatMessage> chatMessages = new ArrayList<>();

                    for (String messageId : messagesIds) {
                        ChatMessage chatMessage = snapshot.child(messageId).getValue(ChatMessage.class);
                        chatMessages.add(chatMessage);
                    }

                    _messagesLiveData.postValue(chatMessages);
                });
    }

    public void listenForMessagesUpdates(ChatGroup currentGroup) {
        chatDB
                .getMessageRecipientsEndpoint()
                .child(currentGroup.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ChatMessageRecipients recipients = snapshot.getValue(ChatMessageRecipients.class);

                        if (recipients != null) {
                            setRecipients(recipients);
                            getMessages(recipients);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void setDialogLastMessage(ChatGroup currentGroup, ChatMessage lastMessage) {
        chatDB
                .getGroupsEndpoint()
                .child(currentGroup.getId())
                .child("lastMsg")
                .setValue(lastMessage)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Logs.error(this, e.getMessage());
                    }
                });
    }

    public Task<Void> setIsLooking(ChatGroup currentGroup, ChatUser currentChatUser, boolean isLooking) {
        return chatDB
                .getGroupsEndpoint()
                .child(currentGroup.getId())
                .child("isLooking")
                .child(currentChatUser.getId())
                .setValue(isLooking);

    }

    public void setGroupUnreadCount(ChatUser currentUser, ChatGroup currentGroup, int unreadCount) {
        chatDB
                .getUserGroupsEndpoint()
                .child(currentUser.getId())
                .child(currentGroup.getId())
                .setValue(unreadCount)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Logs.error(this, e.getMessage());
                    }
                });
    }

    public void cancelNotifications(Context appContext) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(appContext);
        notificationManager.cancel(0);
    }

    public void setCurrentGroup(ChatGroup currentGroup) {
        this._currentGroupLiveData.postValue(currentGroup);
    }

    public void setRecipients(ChatMessageRecipients recipients) {
        this.recipients = recipients;
    }
}
