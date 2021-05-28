package com.example.androidchat2.views.utils.rxfirebase;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import io.reactivex.rxjava3.core.MaybeEmitter;

public class RxHandler<T> implements OnSuccessListener<T>, OnFailureListener, OnCompleteListener<T> {

    private final MaybeEmitter<T> emitter;

    private RxHandler(MaybeEmitter<T> emitter) {
        this.emitter = emitter;
    }

    public static <T> void assignOnTask(MaybeEmitter<T> emitter, Task<T> task) {
        RxHandler<T> handler = new RxHandler<>(emitter);
        task.addOnSuccessListener(handler);
        task.addOnFailureListener(handler);

        try {
            task.addOnCompleteListener(handler);
        } catch (Throwable ignored) {

        }
    }

    @Override
    public void onSuccess(T t) {
        if (t != null) {
            emitter.onSuccess(t);
        }
    }

    @Override
    public void onComplete(@NonNull Task<T> task) {
        emitter.onComplete();
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        if (!emitter.isDisposed()) {
            emitter.onError(e);
        }
    }
}
