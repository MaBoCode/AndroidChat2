package com.example.androidchat2.views.photo.utils;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.androidchat2.core.photo.Photo;
import com.example.androidchat2.databinding.PhotoListItemBinding;
import com.example.androidchat2.views.base.BaseRecyclerViewAdapter;
import com.example.androidchat2.views.base.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

public class PhotoAdapter extends BaseRecyclerViewAdapter<Photo, PhotoListItemBinding> {

    public PhotoAdapter(@NonNull @NotNull DiffUtil.ItemCallback<Photo> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @NotNull
    @Override
    public BaseViewHolder<Photo, PhotoListItemBinding> onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        PhotoListItemBinding binding = PhotoListItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new PhotoViewHolder(binding);
    }
}
