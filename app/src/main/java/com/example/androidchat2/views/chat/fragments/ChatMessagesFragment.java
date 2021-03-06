package com.example.androidchat2.views.chat.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.androidannotations.annotations.EFragment;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@EFragment
public class ChatMessagesFragment extends BaseFragment {

    protected FrgChatMessagesBinding binding;

    protected MainActivityViewModel mainViewModel;
    protected ChatMessagesFragmentViewModel messagesViewModel;

    protected MessagesListAdapter<ChatMessage> messagesListAdapter;

    protected View.OnClickListener onSendMessageClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String messageContent = binding.msgInput.getEditText().getText().toString();
            if (!messageContent.isEmpty()) {
                ChatUser currentChatUser = mainViewModel.unwrapCurrentChatUser();
                ChatGroup currentGroup = messagesViewModel.currentGroupLiveData.getValue();
                messagesViewModel.sendMessage(currentChatUser, currentGroup, messageContent);
                binding.msgInput.getEditText().getText().clear();
            }
        }
    };

    protected View.OnFocusChangeListener onEditTextFocusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            String hint;
            if (hasFocus) {
                hint = getString(R.string.msg_input_hint_focused);
            } else {
                hint = getString(R.string.msg_input_hint_not_focused);
            }
            binding.msgInput.getEditText().setHint(hint);
        }
    };

    protected TextWatcher msgInputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            binding.msgInput.setEndIconActivated(true);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().isEmpty()) {
                binding.msgInput.setEndIconActivated(false);
            }
        }
    };

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FrgChatMessagesBinding.inflate(inflater, container, false);

        messagesViewModel.cancelNotifications(requireContext());

        setupMsgInput();

        retrieveData();

        return binding.getRoot();
    }

    public void setupMsgInput() {
        binding.msgInput.setEndIconActivated(false);
        binding.msgInput.setEndIconOnClickListener(onSendMessageClick);
        binding.msgInput.getEditText().setOnFocusChangeListener(onEditTextFocusChange);
        binding.msgInput.getEditText().addTextChangedListener(msgInputWatcher);
    }

    public void retrieveData() {
        ChatGroup chatGroup = ChatMessagesFragment_Args.fromBundle(getArguments()).getChatGroup();

        if (chatGroup == null) {
            // Deep-linking (notifications)
            Bundle arguments = getArguments();
            String groupId = arguments.getString("group_id");

            mainViewModel.getCurrentChatUser();
            messagesViewModel.getGroupById(groupId);
        } else {

            ChatUser currentChatUser = mainViewModel.currentChatUser.getValue();

            if (currentChatUser == null) {
                throw new NullPointerException("currentChatUser shouldn't be null.");
            }

            updateUnreadCount(currentChatUser, chatGroup);
            setupMessagesAdapter(currentChatUser);
            messagesViewModel.setCurrentGroup(chatGroup);
            messagesViewModel.getUsersFromIds(chatGroup.getUserIds());
            messagesViewModel.listenForMessagesUpdates(chatGroup);
        }
    }

    public void updateUnreadCount(ChatUser currentUser, ChatGroup currentGroup) {

        if (currentGroup.getUnreadCount() > 0) {
            currentGroup.setUnreadCount(0);
            messagesViewModel.setGroupUnreadCount(currentUser, currentGroup, 0);
        }
    }

    public void setupActionBar(ChatUser currentUser, List<ChatUser> chatUsers) {
        MaterialToolbar tlbMessages = binding.tlbMessages;
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(tlbMessages);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StringBuilder tlbTitle = new StringBuilder();
        int i = 0;
        for (IUser user : chatUsers) {
            ChatUser chatUser = (ChatUser) user;
            if (!chatUser.getId().equals(currentUser.getId())) {
                tlbTitle.append(chatUser.getName());

                if (i < chatUsers.size() - 2) {
                    tlbTitle.append(", ");
                }
                i++;
            }
        }
        tlbMessages.setTitle(tlbTitle.toString());

        tlbMessages.setNavigationOnClickListener(view -> Navigation.findNavController(binding.getRoot()).navigateUp());
    }

    public void setupMessagesAdapter(ChatUser currentUser) {
        String senderId = currentUser.getId();

        MessageHolders holders = new MessageHolders()
                .setIncomingTextLayout(R.layout.view_incoming_txt_msg)
                .setOutcomingTextLayout(R.layout.view_outcoming_txt_msg);

        ImageLoader imageLoader = new ChatImageLoader(requireContext());
        messagesListAdapter = new MessagesListAdapter<>(senderId, holders, imageLoader);
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
    public void initViewModels() {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        messagesViewModel = new ViewModelProvider(this).get(ChatMessagesFragmentViewModel.class);
    }

    @Override
    public void subscribeObservers() {

        mainViewModel.currentChatUser.observe(getViewLifecycleOwner(), this::setupMessagesAdapter);

        messagesViewModel.currentGroupLiveData.observe(getViewLifecycleOwner(), chatGroup -> {
            ChatUser currentUser = mainViewModel.unwrapCurrentChatUser();
            messagesViewModel
                    .setIsLooking(chatGroup, currentUser, true)
                    .addOnCompleteListener(task -> updateUnreadCount(currentUser, chatGroup));

            messagesViewModel.listenForMessagesUpdates(chatGroup);
            messagesViewModel.getUsersFromIds(chatGroup.getUserIds());
        });

        messagesViewModel.groupChatUsers.observe(getViewLifecycleOwner(), chatUsers -> {
            setupActionBar(mainViewModel.currentChatUser.getValue(), chatUsers);
        });

        messagesViewModel.messagesLiveData.observe(getViewLifecycleOwner(), this::displayMessages);
    }

    @Override
    public void unsubscribeObservers() {
        mainViewModel.currentChatUser.removeObservers(getViewLifecycleOwner());
        messagesViewModel.messagesLiveData.removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onResume() {
        super.onResume();

        ChatGroup currentGroup = messagesViewModel.currentGroupLiveData.getValue();
        if (currentGroup != null) {
            ChatUser currentUser = mainViewModel.unwrapCurrentChatUser();
            messagesViewModel.setIsLooking(currentGroup, currentUser, true);
        }

    }

    @Override
    public void onPause() {

        ChatGroup currentGroup = messagesViewModel.currentGroupLiveData.getValue();
        ChatUser currentUser = mainViewModel.unwrapCurrentChatUser();

        if (currentGroup != null && currentUser != null) {
            messagesViewModel.setIsLooking(currentGroup, currentUser, false);
        }

        super.onPause();
    }

    @Override
    public void onDestroyView() {

        ViewUtils.hideKeyboard(requireActivity());

        binding.tlbMessages.setNavigationOnClickListener(null);

        unsubscribeObservers();

        super.onDestroyView();
    }
}
