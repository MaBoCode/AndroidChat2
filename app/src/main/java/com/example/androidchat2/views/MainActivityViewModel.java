package com.example.androidchat2.views;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.core.firebase.ChatRealTimeDatabase;
import com.example.androidchat2.injects.base.BaseViewModel;
import com.example.androidchat2.utils.Logs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainActivityViewModel extends BaseViewModel {

    @Inject
    protected FirebaseAuth firebaseAuth;

    @Inject
    protected ChatRealTimeDatabase chatDB;

    protected FirebaseUser currentFirebaseUser;

    protected MutableLiveData<ChatUser> _currentChatUser = new MutableLiveData<>();
    public LiveData<ChatUser> currentChatUser = _currentChatUser;

    @Inject
    public MainActivityViewModel(SavedStateHandle savedStateHandle) {
        super(savedStateHandle);
    }

    public void logoutUser() {
        if (firebaseAuth != null) {
            firebaseAuth.signOut();
            setCurrentFirebaseUser(null);
        }
    }

    public void getCurrentChatUser(FirebaseUser currentFirebaseUser) {
        chatDB
                .getUsersEndpoint()
                .child(currentFirebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        ChatUser chatUser = snapshot.getValue(ChatUser.class);
                        _currentChatUser.postValue(chatUser);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Logs.error(this, error.getMessage());
                    }
                });
    }

    public void setCurrentFirebaseUser(FirebaseUser currentFirebaseUser) {
        this.currentFirebaseUser = currentFirebaseUser;
    }

    public FirebaseUser getCurrentFirebaseUser() {
        return currentFirebaseUser;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }
}
