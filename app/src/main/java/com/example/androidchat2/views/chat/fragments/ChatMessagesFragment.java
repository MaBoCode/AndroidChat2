package com.example.androidchat2.views.chat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidchat2.core.chat.ChatDialog;
import com.example.androidchat2.core.chat.ChatMessage;
import com.example.androidchat2.databinding.FrgChatMessagesBinding;
import com.example.androidchat2.injects.base.BaseFragment;
import com.example.androidchat2.utils.Logs;
import com.example.androidchat2.views.MainActivityViewModel;
import com.example.androidchat2.views.chat.utils.ChatImageLoader;
import com.example.androidchat2.views.chat.viewmodels.ChatMessagesFragmentViewModel;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.androidannotations.annotations.EFragment;
import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class ChatMessagesFragment extends BaseFragment implements MessageInput.InputListener {

    protected FrgChatMessagesBinding binding;

    protected MainActivityViewModel mainViewModel;
    protected ChatMessagesFragmentViewModel messagesViewModel;

    protected MessagesListAdapter<ChatMessage> messagesListAdapter;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FrgChatMessagesBinding.inflate(inflater, container, false);

        binding.msgInput.setInputListener(this);

        ChatDialog chatDialog = ChatMessagesFragment_Args.fromBundle(getArguments()).getChatDialog();
        messagesViewModel.setCurrentDialog(chatDialog);
        messagesViewModel.setCurrentChatUser(mainViewModel.currentChatUser.getValue());

        setupMessagesAdapter();

        return binding.getRoot();
    }

    public void setupMessagesAdapter() {
        String senderId = mainViewModel.currentChatUser.getValue().getId();
        messagesListAdapter = new MessagesListAdapter<>(senderId, new ChatImageLoader(requireContext()));
        binding.chatMessagesLst.setAdapter(messagesListAdapter);
    }

    @Override
    public boolean onSubmit(CharSequence input) {

        if (!input.toString().isEmpty()) {
            messagesViewModel.sendMessage(input.toString());
        }

        return true;
    }

    @Override
    public void initViewModels() {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        messagesViewModel = new ViewModelProvider(this).get(ChatMessagesFragmentViewModel.class);
    }

    @Override
    public void subscribeObservers() {
        messagesViewModel.lastMessageLiveData.observe(getViewLifecycleOwner(), new Observer<ChatMessage>() {
            @Override
            public void onChanged(ChatMessage chatMessage) {
                Logs.debug(this, chatMessage.toString());
                messagesListAdapter.addToStart(chatMessage, true);
            }
        });
    }

    @Override
    public void unsubscribeObservers() {
        messagesViewModel.lastMessageLiveData.removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onDestroyView() {

        unsubscribeObservers();

        super.onDestroyView();
    }
}
