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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatDialogsFragmentViewModel extends BaseViewModel {

    @Inject
    protected ChatRealTimeDatabase chatDB;

    protected MutableLiveData<List<ChatDialog>> _dialogsLiveData = new MutableLiveData<>();
    public LiveData<List<ChatDialog>> dialogsLiveData = _dialogsLiveData;

    @Inject
    public ChatDialogsFragmentViewModel(SavedStateHandle savedStateHandle) {
        super(savedStateHandle);
    }

    public void getDialogs(FirebaseUser currentUser) {
        chatDB
                .getUsersEndpoint()
                .child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        ChatUser user = snapshot.getValue(ChatUser.class);

                        if (user == null) {
                            return;
                        }

                        List<ChatDialog> dialogs = new ArrayList<>();
                        for (String dialogId : user.getDialogIds()) {
                            chatDB
                                    .getDialogsEndpoint()
                                    .child(dialogId)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            ChatDialog dialog = snapshot.getValue(ChatDialog.class);
                                            dialogs.add(dialog);
                                            _dialogsLiveData.postValue(dialogs);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                            Logs.debug(this, error.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Logs.debug(this, error.getMessage());
                    }
                });
    }
}
