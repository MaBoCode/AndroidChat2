package com.example.androidchat2.views.chat.viewmodels;

import android.content.Context;

import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidchat2.core.chat.ChatGroup;
import com.example.androidchat2.core.chat.ChatMessage;
import com.example.androidchat2.core.chat.ChatMessageRecipients;
import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.core.firebase.callbacks.ChatTaskCallback;
import com.example.androidchat2.core.firebase.datasources.ChatDataSource;
import com.example.androidchat2.core.firebase.datasources.ChatUserDataSource;
import com.example.androidchat2.injects.base.BaseViewModel;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatMessagesFragmentViewModel extends BaseViewModel {

    @Inject
    protected ChatUserDataSource userDataSource;

    @Inject
    protected ChatDataSource chatDataSource;

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
        ChatMessage message = chatDataSource.buildMessage(currentGroup.getId(), value, currentChatUser);

        if (recipients != null) {
            recipients.addRecipient(message.getId());
        } else {
            recipients = new ChatMessageRecipients(Arrays.asList(message.getId()));
        }

        chatDataSource.sendMessage(message, currentChatUser, currentGroup, recipients);
    }

    public void getGroupById(String groupId) {
        chatDataSource
                .getGroupById(groupId, new ChatTaskCallback<ChatGroup>() {
                    @Override
                    public void onSuccess(ChatGroup group) {
                        getUsersFromIds(group.getUserIds());

                        _currentGroupLiveData.postValue(group);
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
    }

    public void getUsersFromIds(List<String> userIds) {
        userDataSource
                .getUsersFromIds(userIds, new ChatTaskCallback<List<ChatUser>>() {
                    @Override
                    public void onSuccess(List<ChatUser> users) {
                        _groupChatUsers.postValue(users);
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
    }

    public void getMessages(ChatMessageRecipients chatMessageRecipients) {
        chatDataSource
                .getMessages(chatMessageRecipients, new ChatTaskCallback<List<ChatMessage>>() {
                    @Override
                    public void onSuccess(List<ChatMessage> payload) {
                        _messagesLiveData.postValue(payload);
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
    }

    public void listenForMessagesUpdates(ChatGroup currentGroup) {

        chatDataSource
                .listenForMessagesUpdates(currentGroup.getId(), new ChatDataSource.ChatValueEventCallback<ChatMessageRecipients>() {
                    @Override
                    public void onChange(ChatMessageRecipients recipients) {
                        setRecipients(recipients);
                        getMessages(recipients);
                    }

                    @Override
                    public void onCancel(Exception e) {

                    }
                });
    }

    public Task<Void> setIsLooking(ChatGroup currentGroup, ChatUser currentChatUser, boolean isLooking) {
        return chatDataSource
                .setIsUserCurrentlyLooking(currentGroup.getId(), currentChatUser.getId(), isLooking);
    }

    public void setGroupUnreadCount(ChatUser currentUser, ChatGroup currentGroup, int unreadCount) {
        chatDataSource
                .setGroupUnreadCount(currentUser.getId(), currentGroup.getId(), unreadCount);
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
