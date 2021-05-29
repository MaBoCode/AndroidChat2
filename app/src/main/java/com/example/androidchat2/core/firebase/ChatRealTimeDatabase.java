package com.example.androidchat2.core.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import javax.inject.Inject;

public class ChatRealTimeDatabase extends BaseRealTimeDatabase {

    @Inject
    public ChatRealTimeDatabase(DatabaseReference rootReference) {
        super(rootReference);
    }

    public String getNewKey(DatabaseReference reference) {
        return reference.push().getKey();
    }

    public Task<Void> insertValue(DatabaseReference reference, String id, Object value) {
        return reference
                .child(id)
                .setValue(value);
    }

    public Task<Void> updateValue(DatabaseReference reference, String id, Object newValue) {
        return insertValue(reference, id, newValue);
    }

    public DatabaseReference getUsersEndpoint() {
        return rootReference.child("users");
    }

    public DatabaseReference getUserGroupsEndpoint() {
        return rootReference.child("user_groups");
    }

    public DatabaseReference getGroupsEndpoint() {
        return rootReference.child("groups");
    }

    public DatabaseReference getMessagesEndpoint() {
        return rootReference.child("messages");
    }

    public DatabaseReference getMessageRecipientsEndpoint() {
        return rootReference.child("message_recipients");
    }
}
