package com.example.app_template_java.core.photo;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;

public class PhotoRepository {

    @Inject
    protected PhotoService photoService;

    @Inject
    public PhotoRepository() {
    }

    public Observable<List<Photo>> getPhotos() {
        return photoService.getPhotos();
    }
}
