package com.example.androidchat2.views.chat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.androidchat2.R;
import com.example.androidchat2.core.chat.ChatDialog;
import com.example.androidchat2.databinding.FrgChatDialogsBinding;
import com.example.androidchat2.injects.base.BaseFragment;
import com.example.androidchat2.views.MainActivityViewModel;
import com.example.androidchat2.views.chat.utils.ChatImageLoader;
import com.example.androidchat2.views.chat.viewmodels.ChatDialogsFragmentViewModel;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class ChatDialogsFragment extends BaseFragment implements DialogsListAdapter.OnDialogClickListener<ChatDialog> {

    protected FrgChatDialogsBinding binding;

    protected MainActivityViewModel mainViewModel;
    protected ChatDialogsFragmentViewModel dialogsViewModel;

    protected DialogsListAdapter<ChatDialog> dialogsAdapter;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FrgChatDialogsBinding.inflate(inflater, container, false);

        setupDialogsAdapter();

        return binding.getRoot();
    }

    public void setupDialogsAdapter() {
        dialogsAdapter = new DialogsListAdapter<>(R.layout.itm_dialog_list, new ChatImageLoader(requireContext()));
        dialogsAdapter.setOnDialogClickListener(this);
        binding.chatDialogsLst.setAdapter(dialogsAdapter);
    }

    @Override
    public void onDialogClick(ChatDialog dialog) {
        ChatDialogsFragment_Directions.ToChatMessagesFragment action = ChatDialogsFragment_Directions.toChatMessagesFragment(dialog);
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
        Navigation.findNavController(binding.getRoot()).navigate(R.id.after_logout_action);
    }

    @Override
    public void onResume() {
        super.onResume();

        dialogsViewModel.getDialogs(mainViewModel.getCurrentFirebaseUser());
    }

    public void displayDialogs(List<ChatDialog> chatDialogs) {
        dialogsAdapter.setItems(chatDialogs);
    }

    @Override
    public void initViewModels() {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        dialogsViewModel = new ViewModelProvider(this).get(ChatDialogsFragmentViewModel.class);
    }

    @Override
    public void subscribeObservers() {
        dialogsViewModel.dialogsLiveData.observe(getViewLifecycleOwner(), new Observer<List<ChatDialog>>() {
            @Override
            public void onChanged(List<ChatDialog> chatDialogs) {
                displayDialogs(chatDialogs);
            }
        });
    }

    @Override
    public void unsubscribeObservers() {
        dialogsViewModel.dialogsLiveData.removeObservers(getViewLifecycleOwner());
    }
}
