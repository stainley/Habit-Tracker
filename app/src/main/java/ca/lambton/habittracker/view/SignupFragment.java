package ca.lambton.habittracker.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.lambton.habittracker.databinding.FragmentSignupBinding;
import ca.lambton.habittracker.habit.model.User;

public class SignupFragment extends FragmentActivity {

    FragmentSignupBinding binding;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentSignupBinding.inflate(LayoutInflater.from(getApplicationContext()));
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        binding.signupBtn.setOnClickListener(this::signup);

        binding.loginText.setOnClickListener(this::moveToLogin);
    }

    private void moveToLogin(View view) {
        finish();
    }

    private void signup(View view) {

        String email = binding.emailLoginText.getText().toString();
        String password = binding.passwordLoginText.getText().toString();
        String confirmPassword = binding.confirmPasswordText.getText().toString();

        if (email.equals("") || password.equals("") || confirmPassword.equals("")) {
            Toast.makeText(getApplicationContext(), "Email/Password is empty", Toast.LENGTH_SHORT).show();
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
}