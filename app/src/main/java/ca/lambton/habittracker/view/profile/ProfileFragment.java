package ca.lambton.habittracker.view.profile;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import ca.lambton.habittracker.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseUser currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentProfileBinding.inflate(LayoutInflater.from(requireContext()));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        binding.editNameButton.setOnClickListener(this::editProfileName);
    }

    private void editProfileName(View view) {


        TextInputEditText newEditText = new TextInputEditText(requireContext());
        newEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        newEditText.setSingleLine();
        newEditText.setPadding(50, 0, 50, 32);
        String name = currentUser.getDisplayName();
        newEditText.setText(name != null ? name : "");
        newEditText.setHint("Name");
        newEditText.setPadding(75, newEditText.getPaddingTop(), newEditText.getPaddingRight(), newEditText.getPaddingBottom());


        new MaterialAlertDialogBuilder(requireContext()).setView(newEditText).setMessage("Enter your name").setNeutralButton("Cancel", (dialog, which) -> {
        }).setNegativeButton("Save", (dialog, which) -> {

            String inputText = Objects.requireNonNull(newEditText.getText()).toString();
            if (inputText.equals("")) {
                Toast.makeText(requireContext(), "Couldn't be empty", Toast.LENGTH_SHORT).show();
            } else {
                UserProfileChangeRequest profileChangeNameRequest = new UserProfileChangeRequest.Builder()
                        .setDisplayName(newEditText.getText().toString())
                        .build();

                currentUser.updateProfile(profileChangeNameRequest).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Display name profile updated. Please logout to see changes.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).setCancelable(false).show();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        if (currentUser != null) {
            binding.nameProfile.setText(currentUser.getDisplayName());
            binding.emailProfile.setText(currentUser.getEmail());

            if (currentUser.getPhotoUrl() != null) {
                Picasso.get().load(currentUser.getPhotoUrl()).fit().into(binding.profileImage);
            }
        }

        return binding.getRoot();
    }
}
