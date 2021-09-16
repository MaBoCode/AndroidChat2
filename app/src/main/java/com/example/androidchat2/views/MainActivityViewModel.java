package com.example.androidchat2.views;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.core.firebase.ChatRealTimeDatabase;
import com.example.androidchat2.injects.base.BaseViewModel;
import com.example.androidchat2.utils.Logs;
import com.example.androidchat2.views.utils.rxfirebase.ErrorStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainActivityViewModel extends BaseViewModel {

    @Inject
    protected FirebaseAuth firebaseAuth;

    @Inject
    protected FirebaseMessaging firebaseMessaging;

    @Inject
    protected ChatRealTimeDatabase chatDB;

    protected MutableLiveData<ChatUser> _currentChatUser = new MutableLiveData<>();
    public LiveData<ChatUser> currentChatUser = _currentChatUser;

    protected MutableLiveData<String> _currentNotificationToken = new MutableLiveData<>();
    public LiveData<String> currentNotificationToken = _currentNotificationToken;

    @Inject
    public MainActivityViewModel(SavedStateHandle savedStateHandle) {
        super(savedStateHandle);
    }

    public void logoutUser() {
        if (firebaseAuth != null) {
            firebaseAuth.signOut();
        }
    }

    public void getCurrentChatUser() {
        FirebaseUser currentFirebaseUser = getCurrentFirebaseUser();
        chatDB
                .getUsersEndpoint()
                .child(currentFirebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        ChatUser chatUser = snapshot.getValue(ChatUser.class);

                        String currentNotificationToken = _currentNotificationToken.getValue();

                        if (currentNotificationToken != null
                                && !chatUser.getNotificationTokens().contains(currentNotificationToken)) {
                            chatUser.addNotificationToken(currentNotificationToken);
                            updateUserEntry(chatUser);
                        } else {
                            _currentChatUser.postValue(chatUser);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Logs.error(this, error.getMessage());
                    }
                });
    }

    public void updateUserEntry(ChatUser chatUser) {
        chatDB
                .updateValue(chatDB.getUsersEndpoint(), chatUser.getId(), chatUser)
                .addOnFailureListener(e -> {
                    ErrorStatus errorStatus = new ErrorStatus(e.getMessage());
                    _errorLiveData.postValue(errorStatus);
                })
                .addOnSuccessListener(unused -> {
                    _currentChatUser.postValue(chatUser);
                });
    }

    public void getNotificationRegistrationToken() {
        firebaseMessaging
                .getToken()
                .addOnSuccessListener(token -> {
                    _currentNotificationToken.postValue(token);
                });

    }

    public FirebaseUser getCurrentFirebaseUser() {
        return getFirebaseAuth().getCurrentUser();
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }
}
