package ca.lambton.habittracker.view.fragment.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentComposeBinding;

public class ComposeFragment extends Fragment {

    private FragmentComposeBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentComposeBinding.inflate(LayoutInflater.from(requireContext()));
        binding.btnCompose.setOnClickListener(this::composeNewPost);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return binding.getRoot();
    }

    private void composeNewPost(View view) {
        // TODO: do the logic for create new post
        NavHostFragment.findNavController(this).popBackStack(R.id.nav_community, false);

    }
}
