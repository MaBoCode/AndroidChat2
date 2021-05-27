package com.example.app_template_java.core.photo;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface PhotoService {

    @GET("photos")
    Observable<List<Photo>> getPhotos();

}
