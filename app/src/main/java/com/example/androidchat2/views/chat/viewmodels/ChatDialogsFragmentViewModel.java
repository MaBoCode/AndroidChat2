package com.example.androidchat2.views.chat.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidchat2.core.chat.ChatGroup;
import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.core.chat.ChatUserGroups;
import com.example.androidchat2.core.firebase.ChatRealTimeDatabase;
import com.example.androidchat2.injects.base.BaseViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatDialogsFragmentViewModel extends BaseViewModel {

    @Inject
    protected ChatRealTimeDatabase chatDB;

    protected MutableLiveData<List<ChatGroup>> _groupsLiveData = new MutableLiveData<>();
    public LiveData<List<ChatGroup>> groupsLiveData = _groupsLiveData;

    protected MutableLiveData<ChatGroup> _groupToUpdateLiveData = new MutableLiveData<>();
    public LiveData<ChatGroup> groupToUpdateLiveData = _groupToUpdateLiveData;

    @Inject
    public ChatDialogsFragmentViewModel(SavedStateHandle savedStateHandle) {
        super(savedStateHandle);
    }

    public void getGroups(ChatUserGroups userGroups) {
        chatDB
                .getGroupsEndpoint()
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<ChatGroup> chatGroups = new ArrayList<>();

                    for (Map.Entry<String, Integer> entry : userGroups.getGroups().entrySet()) {
                        String groupId = entry.getKey();
                        int unreadCount = entry.getValue();

                        ChatGroup chatGroup = snapshot.child(groupId).getValue(ChatGroup.class);

                        if (chatGroup != null) {
                            chatGroup.setUnreadCount(unreadCount);
                            chatGroups.add(chatGroup);
                        }
                    }

                    _groupsLiveData.postValue(chatGroups);
                });
    }

    public void listenForGroupCreation(ChatUser chatUser) {
        chatDB
                .getUserGroupsEndpoint()
                .child(chatUser.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Map<String, Integer> groupsMap = new HashMap<>();

                        for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                            String groupId = groupSnapshot.getKey();
                            Long unreadCount = (Long) groupSnapshot.getValue();
                            groupsMap.put(groupId, unreadCount.intValue());
                        }

                        ChatUserGroups userGroups = new ChatUserGroups(groupsMap);
                        listenForGroupsUpdates(userGroups);
                        getGroups(userGroups);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void listenForGroupsUpdates(ChatUserGroups userGroups) {
        for (Map.Entry<String, Integer> entry : userGroups.getGroups().entrySet()) {
            listenForGroupUpdates(entry.getKey(), entry.getValue());
        }
    }

    public void listenForGroupUpdates(String groupId, int unreadCount) {
        chatDB
                .getGroupsEndpoint()
                .child(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ChatGroup group = snapshot.getValue(ChatGroup.class);

                        if (group != null) {
                            group.setUnreadCount(unreadCount);
                            _groupToUpdateLiveData.postValue(group);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
