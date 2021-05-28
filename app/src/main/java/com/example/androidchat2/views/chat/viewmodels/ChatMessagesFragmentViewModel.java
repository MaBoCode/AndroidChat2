package com.example.androidchat2.views.chat.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidchat2.core.chat.ChatDialog;
import com.example.androidchat2.core.chat.ChatMessage;
import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.core.firebase.ChatRealTimeDatabase;
import com.example.androidchat2.injects.base.BaseViewModel;
import com.example.androidchat2.views.utils.rxfirebase.ErrorStatus;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatMessagesFragmentViewModel extends BaseViewModel {

    @Inject
    protected ChatRealTimeDatabase chatDB;

    protected ChatDialog currentDialog;
    protected ChatUser currentChatUser;

    protected MutableLiveData<ChatMessage> _lastMessageLiveData = new MutableLiveData<>();
    public LiveData<ChatMessage> lastMessageLiveData = _lastMessageLiveData;

    @Inject
    public ChatMessagesFragmentViewModel(SavedStateHandle savedStateHandle) {
        super(savedStateHandle);
    }

    public void sendMessage(String value) {
        ChatMessage msg = new ChatMessage();
        msg.setText(value);
        msg.setUser(currentChatUser);
        msg.setCreatedAt(new Date());

        DatabaseReference messagesEndpoint = chatDB.getMessagesEndpoint(currentDialog.getId());
        ErrorStatus msgErrorStatus = new ErrorStatus("Unable to create message.");
        String msgId = chatDB.getNewKey(messagesEndpoint);

        if (msgId == null) {
            _errorLiveData.postValue(msgErrorStatus);
            return;
        }

        msg.setId(msgId);
        chatDB
                .insertValue(messagesEndpoint, msgId, msg)
                .addOnFailureListener(e -> {
                    _errorLiveData.postValue(msgErrorStatus);
                })
                .addOnSuccessListener(unused -> {
                    _lastMessageLiveData.postValue(msg);
                });
    }

    public void setCurrentChatUser(ChatUser currentChatUser) {
        this.currentChatUser = currentChatUser;
    }

    public void setCurrentDialog(ChatDialog currentDialog) {
        this.currentDialog = currentDialog;
    }

    public ChatDialog getCurrentDialog() {
        return currentDialog;
    }
}
