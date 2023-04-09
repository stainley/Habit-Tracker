package ca.lambton.habittracker.habit.view.login;

import android.content.Context;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;

import ca.lambton.habittracker.R;

public class FirebaseUtils {

    public static BeginSignInRequest signInRequest(Context context) {

        return BeginSignInRequest.builder().setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder().setSupported(true).build()).setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder().setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(context.getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false).build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true).build();
    }

}
