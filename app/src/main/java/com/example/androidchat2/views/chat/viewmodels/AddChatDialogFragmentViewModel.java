package com.example.androidchat2.views.chat.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidchat2.core.chat.ChatGroup;
import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.core.chat.ChatUserGroups;
import com.example.androidchat2.core.firebase.ChatRealTimeDatabase;
import com.example.androidchat2.injects.base.BaseViewModel;
import com.example.androidchat2.views.utils.rxfirebase.ErrorStatus;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddChatDialogFragmentViewModel extends BaseViewModel {

    @Inject
    protected ChatRealTimeDatabase chatDB;

    protected ChatUserGroups userGroups = null;

    protected MutableLiveData<List<ChatUser>> _usersLiveData = new MutableLiveData<>();
    public LiveData<List<ChatUser>> usersLiveData = _usersLiveData;

    protected MutableLiveData<ChatGroup> _createdGroupLiveData = new MutableLiveData<>();
    public LiveData<ChatGroup> createdGroupLiveData = _createdGroupLiveData;

    @Inject
    public AddChatDialogFragmentViewModel(SavedStateHandle savedStateHandle) {
        super(savedStateHandle);
    }

    public void getUsers() {
        chatDB.getUsersEndpoint()
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ChatUser> users = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ChatUser user = dataSnapshot.getValue(ChatUser.class);
                        users.add(user);
                    }
                    _usersLiveData.postValue(users);
                });
    }

    public void createNewDialog(String dialogName, List<ChatUser> users) {
        String groupId = chatDB.getNewKey(chatDB.getGroupsEndpoint());

        ErrorStatus dialogErrorStatus = new ErrorStatus("Unable to create dialog.");

        if (groupId == null) {
            _errorLiveData.postValue(dialogErrorStatus);
            return;
        }

        ChatGroup group = new ChatGroup(groupId, dialogName, users);

        if (userGroups == null) {
            userGroups = new ChatUserGroups(Arrays.asList(groupId));
        } else {
            userGroups.addGroup(groupId);
        }

        for (ChatUser user : users) {
            chatDB
                    .getUserGroupsEndpoint()
                    .child(user.getId())
                    .child(groupId)
                    .setValue(0)
                    .addOnFailureListener(e -> _errorLiveData.postValue(dialogErrorStatus));
        }

        chatDB
                .insertValue(chatDB.getGroupsEndpoint(), groupId, group)
                .addOnSuccessListener(unused -> _createdGroupLiveData.postValue(group));
    }

    public void getUserGroups(ChatUser currentChatUser) {
        chatDB
                .getUserGroupsEndpoint()
                .child(currentChatUser.getId())
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    ChatUserGroups userGroups = dataSnapshot.getValue(ChatUserGroups.class);
                    setUserGroups(userGroups);
                });
    }

    public void setUserGroups(ChatUserGroups userGroups) {
        this.userGroups = userGroups;
    }
}
