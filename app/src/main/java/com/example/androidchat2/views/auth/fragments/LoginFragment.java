package com.example.androidchat2.views.auth.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.androidchat2.R;
import com.example.androidchat2.databinding.FrgLoginBinding;
import com.example.androidchat2.injects.base.BaseFragment;
import com.example.androidchat2.views.MainActivityViewModel;
import com.example.androidchat2.views.auth.viewmodels.LoginFragmentViewModel;
import com.example.androidchat2.views.utils.ViewUtils;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class LoginFragment extends BaseFragment {

    protected FrgLoginBinding binding;

    protected MainActivityViewModel mainViewModel;
    protected LoginFragmentViewModel loginViewModel;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FrgLoginBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Click
    public void loginBtnClicked() {
        String email = binding.emailTxtInputLy.getEditText().getText().toString();
        String password = binding.passwordTxtInputLy.getEditText().getText().toString();
        loginViewModel.login(mainViewModel.getFirebaseAuth(), email, password);
    }

    @Click
    public void signupBtnClicked() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.login_to_signup_fragment);
    }

    public void onUserLoggedIn() {
        ViewUtils.hideKeyboard(requireActivity());

        NavController navController = Navigation.findNavController(binding.getRoot());
        navController.navigate(R.id.login_to_chat_dialogs_fragment);
    }

    @Override
    public void initViewModels() {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        loginViewModel = new ViewModelProvider(this).get(LoginFragmentViewModel.class);
    }

    @Override
    public void subscribeObservers() {
        loginViewModel.currentUserLiveData.observe(getViewLifecycleOwner(), firebaseUser -> {
            mainViewModel.setCurrentFirebaseUser(firebaseUser);
            mainViewModel.getCurrentChatUser(firebaseUser);
        });

        mainViewModel.currentChatUser.observe(getViewLifecycleOwner(), chatUser -> onUserLoggedIn());

        loginViewModel.errorLiveData.observe(
                getViewLifecycleOwner(),
                errorStatus -> Toast.makeText(requireContext(), errorStatus.message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void unsubscribeObservers() {
        loginViewModel.currentUserLiveData.removeObservers(getViewLifecycleOwner());
        mainViewModel.currentChatUser.removeObservers(getViewLifecycleOwner());
        loginViewModel.errorLiveData.removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onDestroyView() {

        unsubscribeObservers();

        super.onDestroyView();
    }
}
