package com.example.androidchat2.views.chat.viewmodels;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

    protected MutableLiveData<ChatMessage> _sentMessageLiveData = new MutableLiveData<>();

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
        ChatMessage msg = new ChatMessage(msgId, value, currentChatUser, currentDate, currentGroupId);

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
                .insertValue(chatDB.getMessagesEndpoint(), msg.getId(), msg)
                .addOnSuccessListener(unused -> {
                    _sentMessageLiveData.postValue(msg);
                    setDialogLastMessage(currentGroup, msg);
                });
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

    public void setIsLooking(ChatGroup currentGroup, ChatUser currentChatUser, boolean isLooking) {
        chatDB
                .getGroupsEndpoint()
                .child(currentGroup.getId())
                .child("isLooking")
                .child(currentChatUser.getId())
                .setValue(isLooking)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Logs.error(this, e.getMessage());
                    }
                });

    }

    public void setCurrentGroup(ChatGroup currentGroup) {
        this._currentGroupLiveData.postValue(currentGroup);
    }

    public void setRecipients(ChatMessageRecipients recipients) {
        this.recipients = recipients;
    }
}
