package com.example.androidchat2.core.firebase.callbacks;

public interface ChatTaskCallback<T> {

    void onSuccess(T payload);

    void onFailure(Exception e);
}
