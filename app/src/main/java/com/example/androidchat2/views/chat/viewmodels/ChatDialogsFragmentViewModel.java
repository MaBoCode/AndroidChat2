package com.example.androidchat2.views.chat.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidchat2.core.chat.ChatGroup;
import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.core.chat.ChatUserGroup;
import com.example.androidchat2.core.firebase.ChatRealTimeDatabase;
import com.example.androidchat2.injects.base.BaseViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatDialogsFragmentViewModel extends BaseViewModel {

    @Inject
    protected ChatRealTimeDatabase chatDB;

    protected ChatUser currentChatUser;

    protected MutableLiveData<List<ChatGroup>> _groupsLiveData = new MutableLiveData<>();
    public LiveData<List<ChatGroup>> groupsLiveData = _groupsLiveData;

    protected MutableLiveData<List<ChatUserGroup>> _userGroupsLiveData = new MutableLiveData<>();
    public LiveData<List<ChatUserGroup>> userGroupsLiveData = _userGroupsLiveData;

    @Inject
    public ChatDialogsFragmentViewModel(SavedStateHandle savedStateHandle) {
        super(savedStateHandle);
    }

    public void getUserGroups() {
        chatDB
                .getUserGroupsEndpoint()
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ChatUserGroup> groups = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ChatUserGroup group = dataSnapshot.getValue(ChatUserGroup.class);

                        if (group.getUserId().equals(currentChatUser.getId())) {
                            groups.add(group);
                        }
                    }

                    if (groups.isEmpty()) {
                        return;
                    }

                    _userGroupsLiveData.postValue(groups);
                });
    }

    public void getGroups() {
        chatDB
                .getGroupsEndpoint()
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ChatGroup> groups = new ArrayList<>();

                    List<ChatUserGroup> userGroups = userGroupsLiveData.getValue();
                    for (ChatUserGroup userGroup : userGroups) {
                        DataSnapshot dataSnapshot = snapshot.child(userGroup.getGroupId());
                        ChatGroup group = dataSnapshot.getValue(ChatGroup.class);
                        groups.add(group);
                    }
                    _groupsLiveData.postValue(groups);
                });
    }

    public void listenForGroupsUpdates() {
        List<ChatGroup> groups = groupsLiveData.getValue();
        List<ChatGroup> copy = new ArrayList<>(groups);

        AtomicInteger i = new AtomicInteger(0);
        for (ChatGroup group : copy) {
            chatDB
                    .getGroupsEndpoint()
                    .child(group.getId())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            ChatGroup chatGroup = snapshot.getValue(ChatGroup.class);

                            if (i.get() < groups.size()) {
                                groups.set(i.get(), chatGroup);
                                _groupsLiveData.postValue(groups);
                                i.incrementAndGet();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
        }
    }

    public List<ChatGroup> getDialogChildren(DataSnapshot snapshot) {
        List<ChatGroup> dialogs = new ArrayList<>();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            ChatGroup dialog = dataSnapshot.getValue(ChatGroup.class);
            dialogs.add(dialog);
        }
        return dialogs;
    }

    public void setCurrentChatUser(ChatUser currentChatUser) {
        this.currentChatUser = currentChatUser;
    }
}
