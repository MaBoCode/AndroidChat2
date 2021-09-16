package com.example.androidchat2.views.auth.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.androidchat2.injects.base.BaseViewModel;
import com.example.androidchat2.views.utils.rxfirebase.RxFirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginFragmentViewModel extends BaseViewModel {

    protected MutableLiveData<Boolean> _loginStateLiveData = new MutableLiveData<>();
    public LiveData<Boolean> loginStateLiveData = _loginStateLiveData;

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
                    if (authResult.getUser() != null) {
                        _loginStateLiveData.postValue(true);
                    } else {
                        _loginStateLiveData.postValue(false);
                    }
                }, errorStatusConsumer);
    }
}
