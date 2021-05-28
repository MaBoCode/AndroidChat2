package com.example.androidchat2.injects.base;

import android.app.Activity;
import android.view.View;

import com.example.androidchat2.R;

public interface BaseComponent {

    void initViewModels();

    void subscribeObservers();

    void unsubscribeObservers();

    default void showHideLoader(Activity activity, BaseViewModel.LoadingStatus status) {
        int visibility = status == BaseViewModel.LoadingStatus.LOADING ? View.VISIBLE : View.GONE;
        activity.findViewById(R.id.loader).setVisibility(visibility);
    }

}
