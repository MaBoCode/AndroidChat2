package com.example.androidchat2.core.firebase;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public abstract class BaseRealTimeDatabase implements RealTimeDatabase {

    @Inject
    protected DatabaseReference rootReference;

    protected Map<String, DatabaseReference> endPoints = new HashMap<>();

    public BaseRealTimeDatabase(DatabaseReference rootReference) {
        this.rootReference = rootReference;
    }

    @Override
    public DatabaseReference addEndPoint(String name) {
        DatabaseReference endpoint = rootReference.child(name);
        return endPoints.put(name, endpoint);
    }

    @Override
    public DatabaseReference getEndPointReference(String name) {
        return endPoints.get(name);
    }
}
