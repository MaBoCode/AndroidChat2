package com.example.androidchat2.views.auth.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.androidchat2.R;
import com.example.androidchat2.databinding.FrgSignupBinding;
import com.example.androidchat2.injects.base.BaseFragment;
import com.example.androidchat2.views.MainActivityViewModel;
import com.example.androidchat2.views.auth.viewmodels.SignupFragmentViewModel;
import com.example.androidchat2.views.utils.ViewUtils;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class SignupFragment extends BaseFragment {

    protected FrgSignupBinding binding;

    protected MainActivityViewModel mainViewModel;
    protected SignupFragmentViewModel signupViewModel;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FrgSignupBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Click
    public void signupBtnClicked() {
        String username = binding.usernameTxtInputLy.getEditText().getText().toString();
        String email = binding.emailTxtInputLy.getEditText().getText().toString();
        String password = binding.passwordTxtInputLy.getEditText().getText().toString();
        signupViewModel.signup(mainViewModel.getFirebaseAuth(), username, email, password);
    }

    @Click
    public void loginBtnClicked() {
        Navigation.findNavController(binding.getRoot()).navigate(R.id.signup_to_login_fragment);
    }

    public void onUserSignedUp() {
        ViewUtils.hideKeyboard(requireActivity());
        Navigation.findNavController(binding.getRoot()).navigate(R.id.signup_to_chat_dialogs_fragment);
    }

    @Override
    public void initViewModels() {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        signupViewModel = new ViewModelProvider(this).get(SignupFragmentViewModel.class);
    }

    @Override
    public void subscribeObservers() {
        signupViewModel.signupState.observe(getViewLifecycleOwner(), successState -> {
            if (successState) {
                mainViewModel.getCurrentChatUser();
            }
        });

        mainViewModel.currentChatUser.observe(getViewLifecycleOwner(), chatUser -> onUserSignedUp());

        signupViewModel.errorLiveData.observe(
                getViewLifecycleOwner(),
                errorStatus -> Toast.makeText(requireContext(), errorStatus.message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void unsubscribeObservers() {
        signupViewModel.signupState.removeObservers(getViewLifecycleOwner());
        mainViewModel.currentChatUser.removeObservers(getViewLifecycleOwner());
        signupViewModel.errorLiveData.removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onDestroyView() {

        unsubscribeObservers();

        super.onDestroyView();
    }
}
