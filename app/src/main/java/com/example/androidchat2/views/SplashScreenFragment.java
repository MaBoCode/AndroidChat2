package com.example.androidchat2.views;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.androidchat2.R;
import com.example.androidchat2.databinding.FrgSplashscreenBinding;
import com.example.androidchat2.injects.base.BaseFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.androidannotations.annotations.EFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class SplashScreenFragment extends BaseFragment {

    protected FrgSplashscreenBinding binding;

    protected MainActivityViewModel mainViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FrgSplashscreenBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        new Handler().postDelayed(this::handleUserAuth, 150);
    }

    public void handleUserAuth() {
        FirebaseAuth firebaseAuth = mainViewModel.getFirebaseAuth();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.splash_to_signup_fragment);
        } else {
            mainViewModel.getCurrentChatUser();
        }
    }

    @Override
    public void initViewModels() {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
    }

    @Override
    public void subscribeObservers() {
        mainViewModel.currentChatUser.observe(getViewLifecycleOwner(), chatUser -> Navigation.findNavController(binding.getRoot()).navigate(R.id.splash_to_chat_dialogs_fragment));
    }

    @Override
    public void unsubscribeObservers() {

    }
}
