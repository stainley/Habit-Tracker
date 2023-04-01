package ca.lambton.habittracker.community.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.community.model.Post;
import ca.lambton.habittracker.community.viewmodel.PostViewModel;
import ca.lambton.habittracker.community.viewmodel.PostViewModelFactory;
import ca.lambton.habittracker.databinding.FragmentComposeBinding;
import ca.lambton.habittracker.habit.model.User;

public class ComposeFragment extends Fragment {

    private FragmentComposeBinding binding;
    private PostViewModel postViewModel;
    private FirebaseUser mUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentComposeBinding.inflate(LayoutInflater.from(requireContext()));
        binding.postButton.setOnClickListener(this::composeNewPost);

        postViewModel = new ViewModelProvider(this, new PostViewModelFactory(requireActivity().getApplication())).get(PostViewModel.class);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return binding.getRoot();
    }

    private void composeNewPost(View view) {
        Post post = new Post();
        LocalDateTime today = LocalDateTime.now();
        String message = binding.postEditText.getText().toString();
        if (!message.equals(""))
            post.setMessage(message);

        if (mUser != null) {
            User user = new User();
            user.setName(mUser.getDisplayName());
            user.setPhotoUrl(mUser.getPhotoUrl() != null ? mUser.getPhotoUrl().toString() : "");
            user.setAccountId(mUser.getUid());
            user.setEmail(mUser.getEmail());
            post.setUser(user);
        }

        post.setCreationDate(today.toString());
        postViewModel.createPost(post);

        NavHostFragment.findNavController(this).popBackStack(R.id.nav_community, false);

    }
}
