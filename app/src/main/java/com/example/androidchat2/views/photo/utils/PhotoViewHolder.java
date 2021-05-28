package com.example.androidchat2.views.photo.utils;

import com.example.androidchat2.core.photo.Photo;
import com.example.androidchat2.databinding.PhotoListItemBinding;
import com.example.androidchat2.views.base.BaseViewHolder;

public class PhotoViewHolder extends BaseViewHolder<Photo, PhotoListItemBinding> {

    public PhotoViewHolder(PhotoListItemBinding binding) {
        super(binding);
    }

    @Override
    public void bind(Photo photo) {
        if (photo == null) {
            return;
        }

    }
}
