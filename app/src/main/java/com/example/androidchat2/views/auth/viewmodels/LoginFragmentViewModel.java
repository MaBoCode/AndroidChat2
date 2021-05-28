package com.example.androidchat2.views.auth.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidchat2.injects.base.BaseViewModel;
import com.example.androidchat2.views.utils.rxfirebase.RxFirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginFragmentViewModel extends BaseViewModel {

    protected MutableLiveData<FirebaseUser> _currentUserLiveData = new MutableLiveData<>();
    public LiveData<FirebaseUser> currentUserLiveData = _currentUserLiveData;

    @Inject
    public LoginFragmentViewModel(SavedStateHandle savedStateHandle) {
        super(savedStateHandle);
    }

    public void login(FirebaseAuth firebaseAuth, String email, String password) {
        RxFirebaseAuth
                .logInWithEmailAndPassword(firebaseAuth, email, password)
                .doOnSubscribe(loadingStatusConsumer)
                .doFinally(notLoadingStatusAction)
                .subscribe(authResult -> {
                    _currentUserLiveData.postValue(authResult.getUser());
                }, errorStatusConsumer);
    }
}
