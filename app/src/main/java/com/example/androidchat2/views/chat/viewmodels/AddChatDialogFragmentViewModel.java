package com.example.androidchat2.views.chat.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidchat2.core.chat.ChatDialog;
import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.core.firebase.ChatRealTimeDatabase;
import com.example.androidchat2.injects.base.BaseViewModel;
import com.example.androidchat2.utils.Logs;
import com.example.androidchat2.views.utils.rxfirebase.ErrorStatus;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddChatDialogFragmentViewModel extends BaseViewModel {

    @Inject
    protected ChatRealTimeDatabase chatDB;

    protected MutableLiveData<List<ChatUser>> _usersLiveData = new MutableLiveData<>();
    public LiveData<List<ChatUser>> usersLiveData = _usersLiveData;

    protected MutableLiveData<ChatDialog> _createdDialogLiveData = new MutableLiveData<>();
    public LiveData<ChatDialog> createdDialogLiveData = _createdDialogLiveData;

    @Inject
    public AddChatDialogFragmentViewModel(SavedStateHandle savedStateHandle) {
        super(savedStateHandle);
    }

    public void getUsers() {
        chatDB.getUsersEndpoint().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                List<ChatUser> users = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatUser user = dataSnapshot.getValue(ChatUser.class);
                    users.add(user);
                }
                _usersLiveData.postValue(users);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Logs.error(this, error.getMessage());
            }
        });
    }

    public void createNewDialog(String dialogName, List<ChatUser> users) {
        String dialogId = chatDB.getNewKey(chatDB.getDialogsEndpoint());

        ErrorStatus dialogErrorStatus = new ErrorStatus("Unable to create dialog.");

        if (dialogId == null) {
            _errorLiveData.postValue(dialogErrorStatus);
            return;
        }

        ChatDialog dialog = new ChatDialog(dialogId, dialogName, users);

        chatDB
                .insertValue(chatDB.getDialogsEndpoint(), dialogId, dialog)
                .addOnFailureListener(e -> _errorLiveData.postValue(dialogErrorStatus))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        _createdDialogLiveData.postValue(dialog);

                        // Add dialog id to users in the dialog
                        for (ChatUser user : users) {
                            user.addDialogId(dialogId);
                            chatDB.updateValue(chatDB.getUsersEndpoint(), user.getId(), user);
                        }
                    }
                });

    }
}
