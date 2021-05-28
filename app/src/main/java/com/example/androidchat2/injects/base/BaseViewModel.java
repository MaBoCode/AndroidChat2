package com.example.androidchat2.injects.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.androidchat2.views.utils.rxfirebase.ErrorStatus;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;

public abstract class BaseViewModel extends ViewModel {

    public enum LoadingStatus {
        LOADING,
        NOT_LOADING
    }

    protected SavedStateHandle savedStateHandle;

    protected MutableLiveData<LoadingStatus> _loadingLiveData = new MutableLiveData<>();
    public LiveData<LoadingStatus> loadingLiveData = _loadingLiveData;

    protected MutableLiveData<ErrorStatus> _errorLiveData = new MutableLiveData<>();
    public LiveData<ErrorStatus> errorLiveData = _errorLiveData;

    protected Consumer<Disposable> loadingStatusConsumer = disposable -> _loadingLiveData.postValue(LoadingStatus.LOADING);

    protected Action notLoadingStatusAction = () -> _loadingLiveData.postValue(LoadingStatus.NOT_LOADING);

    protected Consumer<Throwable> errorStatusConsumer = throwable -> {
        ErrorStatus errorStatus = new ErrorStatus(throwable.getMessage());
        _errorLiveData.postValue(errorStatus);
    };

    public BaseViewModel(SavedStateHandle savedStateHandle) {
        this.savedStateHandle = savedStateHandle;
    }
}
