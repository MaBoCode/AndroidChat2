package com.example.androidchat2.views.chat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.androidchat2.R;
import com.example.androidchat2.core.chat.ChatGroup;
import com.example.androidchat2.core.chat.ChatMessage;
import com.example.androidchat2.core.chat.ChatUser;
import com.example.androidchat2.databinding.FrgChatMessagesBinding;
import com.example.androidchat2.injects.base.BaseFragment;
import com.example.androidchat2.views.MainActivityViewModel;
import com.example.androidchat2.views.chat.utils.ChatImageLoader;
import com.example.androidchat2.views.chat.viewmodels.ChatMessagesFragmentViewModel;
import com.example.androidchat2.views.utils.ViewUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.androidannotations.annotations.EFragment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

        ChatGroup chatGroup = ChatMessagesFragment_Args.fromBundle(getArguments()).getChatGroup();
        messagesViewModel.setCurrentGroup(chatGroup);
        messagesViewModel.setCurrentChatUser(mainViewModel.currentChatUser.getValue());

        setupActionBar();
        setupMessagesAdapter();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        messagesViewModel.getUserGroups();
    }

    public void setupActionBar() {
        MaterialToolbar tlbMessages = binding.tlbMessages;
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(tlbMessages);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<? extends IUser> dialogMembers = messagesViewModel.getCurrentGroup().getUsers();
        StringBuilder tlbTitle = new StringBuilder();
        int i = 0;
        for (IUser user : dialogMembers) {
            ChatUser chatUser = (ChatUser) user;
            if (!chatUser.getId().equals(mainViewModel.currentChatUser.getValue().getId())) {
                tlbTitle.append(chatUser.getName());

                if (i < dialogMembers.size() - 2) {
                    tlbTitle.append(", ");
                }
                i++;
            }
        }
        tlbMessages.setTitle(tlbTitle.toString());

        tlbMessages.setNavigationOnClickListener(view -> Navigation.findNavController(binding.getRoot()).navigateUp());
    }

    public void setupMessagesAdapter() {
        String senderId = mainViewModel.currentChatUser.getValue().getId();

        MessageHolders holders = new MessageHolders()
                .setIncomingTextLayout(R.layout.view_incoming_txt_msg)
                .setOutcomingTextLayout(R.layout.view_outcoming_txt_msg);

        messagesListAdapter = new MessagesListAdapter<>(senderId, holders, new ChatImageLoader(requireContext()));
        binding.chatMessagesLst.setAdapter(messagesListAdapter);
    }

    public void displayMessages(List<ChatMessage> messages) {
        if (messagesListAdapter.getMessagesCount() == 0) {
            messagesListAdapter.addToEnd(messages, true);
        } else if (messages.size() - messagesListAdapter.getMessagesCount() == 1) {
            ChatMessage lastMessage = messages.get(messages.size() - 1);
            messagesListAdapter.addToStart(lastMessage, true);
        }
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

        mainViewModel.currentChatUser.observe(getViewLifecycleOwner(), chatUser -> messagesViewModel.setCurrentChatUser(chatUser));

        messagesViewModel.userGroupsLiveData.observe(getViewLifecycleOwner(), chatUserGroups -> {
            messagesViewModel.listenForMessagesUpdates();
        });

        messagesViewModel.messageRecipientsLiveData.observe(getViewLifecycleOwner(), chatMessageRecipients -> messagesViewModel.getMessages());

        messagesViewModel.messagesLiveData.observe(getViewLifecycleOwner(), this::displayMessages);
    }

    @Override
    public void unsubscribeObservers() {
        mainViewModel.currentChatUser.removeObservers(getViewLifecycleOwner());
        messagesViewModel.userGroupsLiveData.removeObservers(getViewLifecycleOwner());
        messagesViewModel.messageRecipientsLiveData.removeObservers(getViewLifecycleOwner());
        messagesViewModel.messagesLiveData.removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onDestroyView() {

        ViewUtils.hideKeyboard(requireActivity());

        binding.tlbMessages.setNavigationOnClickListener(null);

        unsubscribeObservers();

        super.onDestroyView();
    }
}
