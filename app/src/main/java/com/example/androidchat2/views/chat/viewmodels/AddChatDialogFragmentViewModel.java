package com.example.androidchat2.views.chat.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidchat2.core.chat.ChatGroup;
import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.core.chat.ChatUserGroups;
import com.example.androidchat2.core.firebase.callbacks.ChatTaskCallback;
import com.example.androidchat2.core.firebase.databases.ChatRealTimeDatabase;
import com.example.androidchat2.core.firebase.datasources.ChatDataSource;
import com.example.androidchat2.core.firebase.datasources.ChatUserDataSource;
import com.example.androidchat2.injects.base.BaseViewModel;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddChatDialogFragmentViewModel extends BaseViewModel {

    @Inject
    protected ChatUserDataSource userDataSource;

    @Inject
    protected ChatDataSource chatDataSource;

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
        userDataSource
                .getUsers(new ChatTaskCallback<List<ChatUser>>() {
                    @Override
                    public void onSuccess(List<ChatUser> users) {
                        _usersLiveData.postValue(users);
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
    }

    public void createNewDialog(String dialogName, List<ChatUser> users) {
        ChatGroup group = chatDataSource.buildGroup(dialogName, users);

        if (userGroups == null) {
            userGroups = new ChatUserGroups(Arrays.asList(group.getId()));
        } else {
            userGroups.addGroup(group.getId());
        }

        for (ChatUser user : users) {
            chatDB
                    .getUserGroupsEndpoint()
                    .child(user.getId())
                    .child(group.getId())
                    .setValue(0);
        }

        chatDB
                .insertValue(chatDB.getGroupsEndpoint(), group.getId(), group)
                .addOnSuccessListener(unused -> _createdGroupLiveData.postValue(group));
    }

    public void getUserGroups(ChatUser currentChatUser) {
        userDataSource
                .getUserGroups(currentChatUser.getId(), new ChatTaskCallback<ChatUserGroups>() {
                    @Override
                    public void onSuccess(ChatUserGroups userGroups) {
                        setUserGroups(userGroups);
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
    }

    public void setUserGroups(ChatUserGroups userGroups) {
        this.userGroups = userGroups;
    }
}
