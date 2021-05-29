package com.example.androidchat2.views.chat.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidchat2.core.chat.ChatGroup;
import com.example.androidchat2.core.chat.ChatMessage;
import com.example.androidchat2.core.chat.ChatMessageRecipient;
import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.core.chat.ChatUserGroup;
import com.example.androidchat2.core.firebase.ChatRealTimeDatabase;
import com.example.androidchat2.injects.base.BaseViewModel;
import com.example.androidchat2.utils.Logs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatMessagesFragmentViewModel extends BaseViewModel {

    @Inject
    protected ChatRealTimeDatabase chatDB;

    protected ChatGroup currentGroup;
    protected ChatUser currentChatUser;

    protected MutableLiveData<List<ChatUserGroup>> _userGroupsLiveData = new MutableLiveData<>();
    public LiveData<List<ChatUserGroup>> userGroupsLiveData = _userGroupsLiveData;

    protected MutableLiveData<ChatMessage> _sentMessageLiveData = new MutableLiveData<>();
    public LiveData<ChatMessage> sentMessageLiveData = _sentMessageLiveData;

    protected MutableLiveData<List<ChatMessageRecipient>> _messageRecipientsLiveData = new MutableLiveData<>();
    public LiveData<List<ChatMessageRecipient>> messageRecipientsLiveData = _messageRecipientsLiveData;

    protected MutableLiveData<List<ChatMessage>> _messagesLiveData = new MutableLiveData<>();
    public LiveData<List<ChatMessage>> messagesLiveData = _messagesLiveData;

    @Inject
    public ChatMessagesFragmentViewModel(SavedStateHandle savedStateHandle) {
        super(savedStateHandle);
    }

    public void sendMessage(String value) {
        String msgId = chatDB.getNewKey(chatDB.getMessagesEndpoint());

        ChatMessage msg = new ChatMessage(msgId, value, currentChatUser, new Date());

        List<ChatUserGroup> userGroups = userGroupsLiveData.getValue();

        if (userGroups == null) {
            return;
        }

        List<ChatMessageRecipient> messageRecipients = new ArrayList<>();
        for (ChatUserGroup userGroup : userGroups) {
            String recipientId = chatDB.getNewKey(chatDB.getMessageRecipientsEndpoint());
            ChatMessageRecipient messageRecipient = new ChatMessageRecipient(
                    recipientId, msg.getUser().getId(), userGroup.getId(), msg.getId());
            messageRecipients.add(messageRecipient);
        }

        for (ChatMessageRecipient messageRecipient : messageRecipients) {
            chatDB
                    .insertValue(chatDB.getMessageRecipientsEndpoint(), messageRecipient.getId(), messageRecipient);
        }

        chatDB
                .insertValue(chatDB.getMessagesEndpoint(), msg.getId(), msg)
                .addOnSuccessListener(unused -> {
                    _sentMessageLiveData.postValue(msg);
                    setDialogLastMessage(msg);
                });
    }

    public void getUserGroups() {
        chatDB
                .getUserGroupsEndpoint()
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ChatUserGroup> groups = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ChatUserGroup group = dataSnapshot.getValue(ChatUserGroup.class);
                        if (group.getGroupId().equals(currentGroup.getId())) {
                            groups.add(group);
                        }
                    }
                    _userGroupsLiveData.postValue(groups);
                });
    }

    public void getMessages() {
        chatDB
                .getMessagesEndpoint()
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ChatMessage> messages = new ArrayList<>();
                    List<ChatMessageRecipient> recipients = messageRecipientsLiveData.getValue();

                    for (ChatMessageRecipient recipient : recipients) {
                        DataSnapshot dataSnapshot = snapshot.child(recipient.getMessageId());
                        ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                        messages.add(message);
                    }
                    _messagesLiveData.postValue(messages);
                });
    }

    public void listenForMessagesUpdates() {
        ChatUserGroup currentUserGroup = userGroupsLiveData.getValue()
                .stream()
                .filter(chatUserGroup -> chatUserGroup.getUserId().equals(currentChatUser.getId()))
                .findFirst()
                .get();

        chatDB
                .getMessageRecipientsEndpoint()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        List<ChatMessageRecipient> recipients = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ChatMessageRecipient recipient = dataSnapshot.getValue(ChatMessageRecipient.class);

                            if (recipient.getRecipientGroupId().equals(currentUserGroup.getId())) {
                                recipients.add(recipient);
                            }
                        }
                        _messageRecipientsLiveData.postValue(recipients);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    public void setDialogLastMessage(ChatMessage lastMessage) {
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

    public void setCurrentChatUser(ChatUser currentChatUser) {
        this.currentChatUser = currentChatUser;
    }

    public void setCurrentGroup(ChatGroup currentGroup) {
        this.currentGroup = currentGroup;
    }

    public ChatGroup getCurrentGroup() {
        return currentGroup;
    }
}
