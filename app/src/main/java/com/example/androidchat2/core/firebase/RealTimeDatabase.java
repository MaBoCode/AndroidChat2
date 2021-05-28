package com.example.androidchat2.core.firebase;

import com.google.firebase.database.DatabaseReference;

public interface RealTimeDatabase {

    DatabaseReference addEndPoint(String name);

    DatabaseReference getEndPointReference(String name);

}
