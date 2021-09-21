package com.example.androidchat2.views.chat.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidchat2.core.chat.ChatGroup;
import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.core.chat.ChatUserGroups;
import com.example.androidchat2.core.firebase.callbacks.ChatTaskCallback;
import com.example.androidchat2.core.firebase.datasources.ChatDataSource;
import com.example.androidchat2.injects.base.BaseViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatDialogsFragmentViewModel extends BaseViewModel {

    @Inject
    protected ChatDataSource chatDataSource;

    protected MutableLiveData<List<ChatGroup>> _groupsLiveData = new MutableLiveData<>();
    public LiveData<List<ChatGroup>> groupsLiveData = _groupsLiveData;

    protected MutableLiveData<ChatGroup> _groupToUpdateLiveData = new MutableLiveData<>();
    public LiveData<ChatGroup> groupToUpdateLiveData = _groupToUpdateLiveData;

    @Inject
    public ChatDialogsFragmentViewModel(SavedStateHandle savedStateHandle) {
        super(savedStateHandle);
    }

    public void getGroups(ChatUserGroups userGroups) {
        chatDataSource
                .getGroups(userGroups, new ChatTaskCallback<List<ChatGroup>>() {
                    @Override
                    public void onSuccess(List<ChatGroup> groups) {
                        _groupsLiveData.postValue(groups);
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
    }

    public void listenForGroupCreation(ChatUser chatUser) {

        chatDataSource
                .listenForGroupCreation(chatUser.getId(), new ChatDataSource.ChatValueEventCallback<ChatUserGroups>() {
                    @Override
                    public void onChange(ChatUserGroups userGroups) {
                        listenForGroupsUpdates(userGroups);
                        getGroups(userGroups);
                    }

                    @Override
                    public void onCancel(Exception e) {

                    }
                });
    }

    public void listenForGroupsUpdates(ChatUserGroups userGroups) {
        chatDataSource
                .listenForGroupsUpdates(userGroups, new ChatDataSource.ChatValueEventCallback<ChatGroup>() {
                    @Override
                    public void onChange(ChatGroup updatedGroup) {
                        _groupToUpdateLiveData.postValue(updatedGroup);
                    }

                    @Override
                    public void onCancel(Exception e) {

                    }
                });
    }
}
