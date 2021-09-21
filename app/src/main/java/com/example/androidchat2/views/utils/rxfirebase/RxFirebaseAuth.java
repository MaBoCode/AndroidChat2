package com.example.androidchat2.views.utils.rxfirebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

public class RxFirebaseAuth {

    public static Maybe<AuthResult> signUpWithEmailAndPassword(FirebaseAuth firebaseAuth, String email, String password) {
        return Maybe.create(emitter -> {
            Task<AuthResult> task = firebaseAuth.createUserWithEmailAndPassword(email, password);
            RxHandler.assignOnTask(emitter, task);
        });
    }

    public static Maybe<AuthResult> logInWithEmailAndPassword(FirebaseAuth firebaseAuth, String email, String password) {
        return Maybe.create(emitter -> {
            Task<AuthResult> task = firebaseAuth.signInWithEmailAndPassword(email, password);
            RxHandler.assignOnTask(emitter, task);
        });
    }

    public static Observable<FirebaseAuth> observableAuthState(FirebaseAuth firebaseAuth) {
        return Observable.create(emitter -> {
            FirebaseAuth.AuthStateListener authStateListener = emitter::onNext;

            firebaseAuth.addAuthStateListener(authStateListener);
            emitter.setCancellable(() -> firebaseAuth.removeAuthStateListener(authStateListener));
        });
    }

}
