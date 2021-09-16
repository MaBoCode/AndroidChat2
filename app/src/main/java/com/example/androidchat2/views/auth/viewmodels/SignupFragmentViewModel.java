package com.example.androidchat2.views.auth.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.core.firebase.ChatRealTimeDatabase;
import com.example.androidchat2.injects.base.BaseViewModel;
import com.example.androidchat2.views.utils.rxfirebase.ErrorStatus;
import com.example.androidchat2.views.utils.rxfirebase.RxFirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SignupFragmentViewModel extends BaseViewModel {

    @Inject
    protected ChatRealTimeDatabase chatDB;

    protected MutableLiveData<Boolean> _signupState = new MutableLiveData<>();
    public LiveData<Boolean> signupState = _signupState;

    @Inject
    public SignupFragmentViewModel(SavedStateHandle savedStateHandle) {
        super(savedStateHandle);
    }

    public void signup(FirebaseAuth firebaseAuth, String username,
                       String email, String password) {
        RxFirebaseAuth
                .signUpWithEmailAndPassword(firebaseAuth, email, password)
                .doOnSubscribe(loadingStatusConsumer)
                .doFinally(notLoadingStatusAction)
                .subscribe(authResult -> {
                    updateUserProfile(authResult.getUser(), username);
                }, errorStatusConsumer);
    }

    public void updateUserProfile(FirebaseUser currentUser, String username) {
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build();
        currentUser
                .updateProfile(profileChangeRequest)
                .addOnSuccessListener(unused -> addUserToRealtimeDB(currentUser));
    }

    public void addUserToRealtimeDB(FirebaseUser firebaseUser) {
        ChatUser user = new ChatUser(firebaseUser);
        chatDB
                .insertValue(chatDB.getUsersEndpoint(), firebaseUser.getUid(), user)
                .addOnFailureListener(e -> {
                    ErrorStatus errorStatus = new ErrorStatus(e.getMessage());
                    _errorLiveData.postValue(errorStatus);
                    _signupState.postValue(false);
                })
                .addOnSuccessListener(unused -> {
                    _signupState.postValue(true);
                });
    }
}
