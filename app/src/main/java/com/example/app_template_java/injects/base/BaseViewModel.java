package com.example.app_template_java.injects.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public abstract class BaseViewModel extends ViewModel {

    public enum LoadingStatus {
        LOADING,
        NOT_LOADING
    }

    protected SavedStateHandle savedStateHandle;

    protected MutableLiveData<LoadingStatus> _loadingLiveData = new MutableLiveData<>();
    public LiveData<LoadingStatus> loadingLiveData = _loadingLiveData;

}
