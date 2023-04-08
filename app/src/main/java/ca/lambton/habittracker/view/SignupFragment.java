package ca.lambton.habittracker.view;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import ca.lambton.habittracker.MainActivity;
import ca.lambton.habittracker.databinding.FragmentSignupBinding;
import ca.lambton.habittracker.habit.model.User;
import ca.lambton.habittracker.view.login.FirebaseUtils;
import ca.lambton.habittracker.view.login.LoginFragment;

public class SignupFragment extends FragmentActivity {

    private final static String TAG = SignupFragment.class.getSimpleName();
    private FragmentSignupBinding binding;
    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;

    private static final int REQ_ONE_TAP = 3;
    private boolean showOneTapUI = true;
    private BeginSignInRequest signInRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentSignupBinding.inflate(LayoutInflater.from(getApplicationContext()));
        setContentView(binding.getRoot());

        binding.forgotPassword.setMovementMethod(LinkMovementMethod.getInstance());

        binding.signupBtn.setOnClickListener(this::signup);

        binding.loginText.setOnClickListener(this::moveToLogin);

        binding.forgotPassword.setOnClickListener(this::forgotPassword);

        binding.loginGoogle.setOnClickListener(this::loginWithGoogle);

        mAuth = FirebaseAuth.getInstance();
        oneTapClient = Identity.getSignInClient(this);

        signInRequest = FirebaseUtils.signInRequest(this);

    }

    private void moveToLogin(View view) {
        finish();
    }

    private void forgotPassword(View view) {
        String emailAddress = binding.emailLoginText.getText().toString();

        if (emailAddress.equals("")) {
            binding.emailLoginText.setError("Email is required");
            Toast.makeText(getApplicationContext(), "Email is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Snackbar.make(view, "Would you like to reset your password?", Toast.LENGTH_SHORT).setAction("Yes", v -> {
            mAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email sent.");
                    Toast.makeText(getApplicationContext(), "Email sent.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Email not found", Toast.LENGTH_LONG).show();
                }
            });
        }).setAnchorView(binding.loginText).show();
    }

    private void loginWithGoogle(View view) {

        oneTapClient.beginSignIn(signInRequest).addOnSuccessListener(SignupFragment.this, result -> {
            try {
                startIntentSenderForResult(result.getPendingIntent().getIntentSender(), REQ_ONE_TAP, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.e("TAG", "Couldn't start One Tap UI: " + e.getLocalizedMessage());
            }
        }).addOnFailureListener(SignupFragment.this, e -> {
            // No saved credentials found. Launch the One Tap sign-up flow, or
            // do nothing and continue presenting the signed-out UI.
            Log.d("TAG", e.getLocalizedMessage());
        });
    }

    private void signup(View view) {

        String email = binding.emailLoginText.getText().toString();
        String password = binding.passwordLoginText.getText().toString();
        String confirmPassword = binding.confirmPasswordText.getText().toString();

        if (email.equals("") || password.equals("") || confirmPassword.equals("")) {
            binding.emailLoginText.setError("Email is required");
            binding.passwordLoginText.setError("Password is required");
            binding.confirmPasswordText.setError("Confirm Password is required");
            return;
        }

        if (password.equals(confirmPassword)) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Sign in successful
                    Toast.makeText(getApplicationContext(), "User/Pass has been created successful", Toast.LENGTH_SHORT).show();

                    FirebaseUser currentUser = task.getResult().getUser();
                    if (currentUser != null) {

                        User user = new User();
                        user.setAccountId(currentUser.getUid());
                        user.setEmail(currentUser.getEmail());

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user", user);

                        Intent loginIntent = new Intent(getApplicationContext(), LoginFragment.class);
                        loginIntent.putExtras(bundle);

                        startActivity(loginIntent);
                    }
                } else {
                    // sign in failed
                    Toast.makeText(getApplicationContext(), "User/Password incorrect", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                String username = credential.getId();
                String password = credential.getPassword();
                if (idToken != null) {
                    // Got an ID token from Google. Use it to authenticate
                    // with your backend.
                    Log.d("TAG", "Got ID token.");

                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    mAuth.signInWithCredential(firebaseCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);

                                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(mainIntent);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInWithCredential:failure", task.getException());
                                updateUI(null);
                            }
                        }
                    });
                } else if (password != null) {
                    // Got a saved username and password. Use them to authenticate
                    // with your backend.
                    Log.d("TAG", "Got password.");
                }
            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case CommonStatusCodes.CANCELED:
                        Log.d("TAG", "One-tap dialog was closed.");
                        // Don't re-prompt the user.
                        showOneTapUI = false;
                        break;
                    case CommonStatusCodes.NETWORK_ERROR:
                        Log.d("TAG", "One-tap encountered a network error.");
                        // Try again or just ignore.
                        break;
                    default:
                        Log.d("TAG", "Couldn't get credential from result." + e.getLocalizedMessage());
                        break;
                }
                Toast.makeText(this, "Login aborted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            User user = new User();
            user.setAccountId(currentUser.getUid());
            user.setEmail(currentUser.getEmail());
            user.setName(currentUser.getDisplayName());
            if (currentUser.getPhotoUrl() != null)
                user.setPhotoUrl(Objects.requireNonNull(currentUser.getPhotoUrl()).toString());

            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);

            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            mainIntent.putExtras(bundle);

            startActivity(mainIntent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }
}