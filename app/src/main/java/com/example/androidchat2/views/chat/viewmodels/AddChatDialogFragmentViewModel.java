package com.example.androidchat2.views.chat.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidchat2.core.chat.ChatGroup;
import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.core.chat.ChatUserGroup;
import com.example.androidchat2.core.firebase.ChatRealTimeDatabase;
import com.example.androidchat2.injects.base.BaseViewModel;
import com.example.androidchat2.views.utils.rxfirebase.ErrorStatus;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddChatDialogFragmentViewModel extends BaseViewModel {

    @Inject
    protected ChatRealTimeDatabase chatDB;

    protected ChatUser currentChatUser;

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
        List<ChatUserGroup> userGroups = new ArrayList<>();

        for (ChatUser user : users) {
            String userGroupId = chatDB.getNewKey(chatDB.getUserGroupsEndpoint());
            ChatUserGroup userGroup = new ChatUserGroup(userGroupId, user.getId(), groupId);
            userGroups.add(userGroup);
        }

        for (ChatUserGroup userGroup : userGroups) {
            chatDB
                    .insertValue(chatDB.getUserGroupsEndpoint(), userGroup.getId(), userGroup);
        }

        chatDB
                .insertValue(chatDB.getGroupsEndpoint(), groupId, group)
                .addOnSuccessListener(unused -> _createdGroupLiveData.postValue(group));
    }

    public void setCurrentChatUser(ChatUser currentChatUser) {
        this.currentChatUser = currentChatUser;
    }
}
