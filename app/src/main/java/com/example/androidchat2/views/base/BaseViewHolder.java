package com.example.androidchat2.views.base;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

public abstract class BaseViewHolder<T, V extends ViewBinding> extends RecyclerView.ViewHolder {

    protected V binding;

    public BaseViewHolder(V binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public abstract void bind(final T object);
}
