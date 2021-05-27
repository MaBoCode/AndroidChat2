package com.example.app_template_java.core.user;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface UserService {

    @GET("users")
    Observable<List<User>> getUsers();

}
