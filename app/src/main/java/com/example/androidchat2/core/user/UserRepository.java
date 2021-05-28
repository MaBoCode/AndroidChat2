package com.example.androidchat2.core.user;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;

public class UserRepository {

    @Inject
    protected UserService userService;

    @Inject
    public UserRepository() {
    }

    public Observable<List<User>> getUsers() {
        return userService.getUsers();
    }
}
