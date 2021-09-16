package com.example.androidchat2.views.chat.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidchat2.R;
import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.databinding.DlgAddChatDialogBinding;
import com.example.androidchat2.injects.base.BaseDialogFragment;
import com.example.androidchat2.views.MainActivityViewModel;
import com.example.androidchat2.views.chat.viewmodels.AddChatDialogFragmentViewModel;
import com.example.androidchat2.views.chat.viewmodels.ChatDialogsFragmentViewModel;
import com.example.androidchat2.views.utils.rxfirebase.ErrorStatus;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class AddChatDialogDialogFragment extends BaseDialogFragment {

    protected DlgAddChatDialogBinding binding;

    protected MainActivityViewModel mainViewModel;
    protected ChatDialogsFragmentViewModel dialogsViewModel;
    protected AddChatDialogFragmentViewModel addDialogViewModel;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = DlgAddChatDialogBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void setupUsersAdapter(List<ChatUser> users) {
        List<String> emails = new ArrayList<>();
        for (ChatUser user : users) {
            if (!user.getId().contentEquals(mainViewModel.getCurrentFirebaseUser().getUid())) {
                emails.add(user.getName());
            }
        }
        ArrayAdapter<String> usersAdapter = new ArrayAdapter<>(requireContext(), R.layout.itm_user_dropdown, emails);
        binding.emailAutoCompleteTxtView.setAdapter(usersAdapter);
    }

    @Click
    public void addDialogBtnClicked() {
        String dialogName = binding.dialogNameTxtInputLy.getEditText().getText().toString();
        String userEmail = binding.userEmailTxtInputLy.getEditText().getText().toString();

        if (dialogName.isEmpty()) {
            ErrorStatus errorStatus = new ErrorStatus("Dialog name can't be empty.");
            displayError(errorStatus);
            return;
        }

        if (userEmail.isEmpty()) {
            ErrorStatus errorStatus = new ErrorStatus("You must select a user email to chat with.");
            displayError(errorStatus);
            return;
        }

        ChatUser selectedUser = null;
        for (ChatUser user : addDialogViewModel.usersLiveData.getValue()) {
            if (user.getName().contentEquals(userEmail)) {
                selectedUser = user;
                break;
            }
        }
        Optional<ChatUser> currentUser = addDialogViewModel.usersLiveData.getValue()
                .stream()
                .filter(chatUser -> chatUser.getId().contentEquals(mainViewModel.getCurrentFirebaseUser().getUid()))
                .findFirst();

        if (!currentUser.isPresent()) {
            return;
        }

        addDialogViewModel.createNewDialog(dialogName, Arrays.asList(currentUser.get(), selectedUser));
    }

    public void displayError(ErrorStatus errorStatus) {
        Toast.makeText(requireContext(), errorStatus.message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();

        addDialogViewModel.getUsers();
    }

    @Override
    public void initViewModels() {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        addDialogViewModel = new ViewModelProvider(requireActivity()).get(AddChatDialogFragmentViewModel.class);
    }

    @Override
    public void subscribeObservers() {
        addDialogViewModel.usersLiveData.observe(getViewLifecycleOwner(), this::setupUsersAdapter);

        addDialogViewModel.createdGroupLiveData.observe(getViewLifecycleOwner(), chatDialog -> mainViewModel.getCurrentChatUser());

        mainViewModel.currentChatUser.observe(getViewLifecycleOwner(), chatUser -> {
            addDialogViewModel.setCurrentChatUser(chatUser);
            if (addDialogViewModel.createdGroupLiveData.getValue() != null) {
                dismissDialog();
            }
        });

        addDialogViewModel.errorLiveData.observe(getViewLifecycleOwner(), errorStatus -> displayError(errorStatus));
    }

    @Override
    public void unsubscribeObservers() {
        mainViewModel.currentChatUser.removeObservers(getViewLifecycleOwner());
        addDialogViewModel.errorLiveData.removeObservers(getViewLifecycleOwner());
        addDialogViewModel.usersLiveData.removeObservers(getViewLifecycleOwner());
    }

    public void dismissDialog() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {

        unsubscribeObservers();

        super.onDestroyView();
    }
}
