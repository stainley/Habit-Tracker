package ca.lambton.habittracker.view.fragment.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.lambton.habittracker.databinding.FragmentCommunityBinding;

public class CommunityFragment extends Fragment {

    private FragmentCommunityBinding binding;
    private FirebaseUser firebaseUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentCommunityBinding.inflate(LayoutInflater.from(requireContext()));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (firebaseUser != null) {
            firebaseUser.getDisplayName();
            binding.userProfile.setText(firebaseUser.getDisplayName() == null ? "" : firebaseUser.getDisplayName());
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnCompose.setOnClickListener(this::newPost);
    }

    private void newPost(View view) {
        NavDirections navNewPostDirections = CommunityFragmentDirections.actionNavCommunityToNavCompose();
        Navigation.findNavController(requireView()).navigate(navNewPostDirections);
    }
}
