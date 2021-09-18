package com.example.androidchat2.views.chat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.androidchat2.R;
import com.example.androidchat2.core.chat.ChatGroup;
import com.example.androidchat2.databinding.FrgChatDialogsBinding;
import com.example.androidchat2.injects.base.BaseFragment;
import com.example.androidchat2.views.MainActivityViewModel;
import com.example.androidchat2.views.chat.utils.ChatImageLoader;
import com.example.androidchat2.views.chat.viewmodels.AddChatDialogFragmentViewModel;
import com.example.androidchat2.views.chat.viewmodels.ChatDialogsFragmentViewModel;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class ChatDialogsFragment extends BaseFragment implements DialogsListAdapter.OnDialogClickListener<ChatGroup> {

    protected FrgChatDialogsBinding binding;

    protected MainActivityViewModel mainViewModel;
    protected ChatDialogsFragmentViewModel dialogsViewModel;
    protected AddChatDialogFragmentViewModel addDialogViewModel;

    protected DialogsListAdapter<ChatGroup> dialogsAdapter;

    boolean isInit = true;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FrgChatDialogsBinding.inflate(inflater, container, false);

        dialogsViewModel.setCurrentChatUser(mainViewModel.currentChatUser.getValue());

        setupDialogsAdapter();

        return binding.getRoot();
    }

    public void setupDialogsAdapter() {
        dialogsAdapter = new DialogsListAdapter<>(R.layout.itm_dialog_list, new ChatImageLoader(requireContext()));
        dialogsAdapter.setOnDialogClickListener(this);
        binding.chatDialogsLst.setAdapter(dialogsAdapter);
    }

    @Override
    public void onDialogClick(ChatGroup dialog) {
        ChatDialogsFragment_Directions.ToChatMessagesFragment action = ChatDialogsFragment_Directions.toChatMessagesFragment();
        action.setChatGroup(dialog);
        Navigation.findNavController(binding.getRoot()).navigate(action);
    }

    @Click
    public void addDialogBtnClicked() {
        NavController navController = Navigation.findNavController(binding.getRoot());
        navController.navigate(R.id.add_chat_dialog_action);
    }

    @Click
    public void logoutBtnClicked() {
        mainViewModel.logoutUser();

        NavController navController = Navigation.findNavController(binding.getRoot());
        navController.navigate(R.id.loginFragment);
    }

    public void displayDialogs(List<ChatGroup> chatGroups) {
        dialogsAdapter.setItems(chatGroups);
    }

    @Override
    public void onStart() {

        dialogsViewModel.getUserGroups();

        super.onStart();
    }

    @Override
    public void initViewModels() {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        dialogsViewModel = new ViewModelProvider(this).get(ChatDialogsFragmentViewModel.class);
        addDialogViewModel = new ViewModelProvider(requireActivity()).get(AddChatDialogFragmentViewModel.class);
    }

    @Override
    public void subscribeObservers() {

        mainViewModel.currentChatUser.observe(getViewLifecycleOwner(), chatUser -> {
            dialogsViewModel.setCurrentChatUser(chatUser);
        });

        dialogsViewModel.userGroupsLiveData.observe(getViewLifecycleOwner(), chatUserGroups -> dialogsViewModel.getGroups());

        dialogsViewModel.groupsLiveData.observe(getViewLifecycleOwner(), chatGroups -> {

            if (isInit) {
                dialogsViewModel.listenForGroupsUpdates();
                isInit = false;
            }

            displayDialogs(chatGroups);
        });

        addDialogViewModel.createdGroupLiveData.observe(getViewLifecycleOwner(), chatGroup -> dialogsAdapter.addItem(chatGroup));
    }

    @Override
    public void unsubscribeObservers() {
        dialogsViewModel.groupsLiveData.removeObservers(getViewLifecycleOwner());
    }
}
