package com.example.app_template_java.views.utils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.example.app_template_java.views.base.BaseRecyclerViewAdapter;
import com.facebook.shimmer.Shimmer;

public class RecyclerViewUtils {

    protected static Shimmer.AlphaHighlightBuilder builder = null;

    public static <T, V extends ViewBinding> void setupAdapter(RecyclerView recyclerView, LinearLayoutManager layoutManager, BaseRecyclerViewAdapter<T, V> adapter) {
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public static Shimmer.AlphaHighlightBuilder getShimmer() {
        if (builder == null) {
            builder = new Shimmer.AlphaHighlightBuilder()
                    .setAutoStart(false)
                    .setDuration(1000L)
                    .setTilt(0f)
                    .setDropoff(1f);
        }
        return builder;
    }

}
