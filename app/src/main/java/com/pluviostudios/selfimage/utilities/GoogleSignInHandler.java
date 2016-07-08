package com.pluviostudios.selfimage.utilities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.pluviostudios.selfimage.R;

/**
 * Created by spectre on 7/6/16.
 */
public class GoogleSignInHandler {

    public static final String REFERENCE_ID = "GoogleSignInHandler";
    public static final int GOOGLE_SIGN_IN_REQUEST_CODE = 11;

    private static Activity sContext;

    private static GoogleApiClient mGoogleApiClient;
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static OnSignInComplete mOnSignInComplete;

    public static void setOnSignInComplete(OnSignInComplete onSignInComplete) {
        mOnSignInComplete = onSignInComplete;
    }

    public interface OnSignInComplete {
        void onSignInComplete(GoogleSignInAccount googleSignInAccount);
    }

    public static void signIn(Activity context) {

        sContext = context;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        context.startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);

    }


    public static void onActivityResult(int requestCode, int resultCode, Intent data) {

        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            // Google Sign In was successful, authenticate with Firebase
            firebaseAuthWithGoogle(result.getSignInAccount());
        }

    }

    private static void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(sContext, new OnCompleteListener<AuthResult>() {

                    GoogleSignInAccount mGoogleSignInAccount;

                    public OnCompleteListener<AuthResult> setGoogleSignInAccount(GoogleSignInAccount googleSignInAccount) {
                        mGoogleSignInAccount = googleSignInAccount;
                        return this;
                    }

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (mOnSignInComplete != null)
                                mOnSignInComplete.onSignInComplete(null);

                        } else {

                            // Login Success!!!
                            if (mOnSignInComplete != null)
                                mOnSignInComplete.onSignInComplete(mGoogleSignInAccount);

                        }

                    }
                }.setGoogleSignInAccount(acct));
    }

}
