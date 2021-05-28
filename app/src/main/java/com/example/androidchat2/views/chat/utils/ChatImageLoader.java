package com.example.androidchat2.views.chat.utils;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.stfalcon.chatkit.commons.ImageLoader;

public class ChatImageLoader implements ImageLoader {

    protected Context context;

    public ChatImageLoader(Context context) {
        this.context = context;
    }

    @Override
    public void loadImage(ImageView imageView, @Nullable @org.jetbrains.annotations.Nullable String url, @Nullable @org.jetbrains.annotations.Nullable Object payload) {
        Glide
                .with(context)
                .load(url)
                .into(imageView);
    }
}
