package com.omar.tictactoexo.firbasehelper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthHelper {
    private final FirebaseAuth mAuth;
    private FirebaseUser user;

    public interface AuthStatus{
        void onAuthSuccess(FirebaseUser firebaseUser);
    }
    public FirebaseAuthHelper() {
        mAuth = FirebaseAuth.getInstance();

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Log.e("Logout", " user null");
                }
                else {
              //      Log.e("Login", " user id " + user.getUid());

                }
            }        });


    }

    public void signIn(final AuthStatus authStatus){
        mAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                user = mAuth.getCurrentUser();
                authStatus.onAuthSuccess(user);
            }
        });

    }

    public void signOut(){
        mAuth.signOut();
    }

    public FirebaseUser getUser() {
        user = mAuth.getCurrentUser();
        return user;
    }
}
