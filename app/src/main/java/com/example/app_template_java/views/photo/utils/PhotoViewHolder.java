package com.example.app_template_java.views.photo.utils;

import com.example.app_template_java.core.photo.Photo;
import com.example.app_template_java.databinding.PhotoListItemBinding;
import com.example.app_template_java.views.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

public class PhotoViewHolder extends BaseViewHolder<Photo, PhotoListItemBinding> {

    public PhotoViewHolder(PhotoListItemBinding binding) {
        super(binding);
    }

    @Override
    public void bind(Photo photo) {
        if (photo == null) {
            return;
        }

        Picasso
                .get()
                .load(photo.thumbnailUrl)
                .into(binding.imgView);
    }
}
