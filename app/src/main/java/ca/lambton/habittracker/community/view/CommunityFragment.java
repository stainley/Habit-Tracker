package ca.lambton.habittracker.community.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.community.model.PostComment;
import ca.lambton.habittracker.community.view.adapter.CommunityAdapter;
import ca.lambton.habittracker.community.viewmodel.PostViewModel;
import ca.lambton.habittracker.community.viewmodel.PostViewModelFactory;
import ca.lambton.habittracker.databinding.FragmentCommunityBinding;

public class CommunityFragment extends Fragment {

    private FragmentCommunityBinding binding;
    private FirebaseUser firebaseUser;
    private CommunityAdapter communityAdapter;
    private final List<PostComment> posts = new ArrayList<>();
    private PostViewModel postViewModel;
    private RecyclerView recyclerView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentCommunityBinding.inflate(LayoutInflater.from(requireContext()));
        postViewModel = new ViewModelProvider(getViewModelStore(), new PostViewModelFactory(requireActivity().getApplication())).get(PostViewModel.class);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        recyclerView = binding.recyclePosts;
        SwipeRefreshLayout swipeUpdate = binding.swipeUpdate;

        swipeUpdate.setOnRefreshListener(() -> {

            postViewModel.fetchAllPostWithComments().observe(getViewLifecycleOwner(), postComments -> {
                posts.clear();
                posts.addAll(postComments);
                communityAdapter.notifyItemRangeChanged(0, postComments.size());

            });
            // stop refresh animation
            swipeUpdate.setRefreshing(false);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(communityAdapter);
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
        communityAdapter = new CommunityAdapter(posts, this::onMoreOptionCallback);
        recyclerView.setAdapter(communityAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        postViewModel.fetchAllPostWithComments().observe(getViewLifecycleOwner(), postComments -> {
            posts.clear();
            posts.addAll(postComments);
            communityAdapter.notifyItemRangeChanged(0, postComments.size());

        });
    }

    private void newPost(View view) {
        NavDirections navNewPostDirections = CommunityFragmentDirections.actionNavCommunityToNavCompose();
        Navigation.findNavController(requireView()).navigate(navNewPostDirections);
    }

    private void onMoreOptionCallback(ImageButton communityButton, int position) {
        if (firebaseUser.getUid().equals(this.posts.get(position).post.getUser().getAccountId())) {
            communityButton.setVisibility(View.VISIBLE);
        }
        communityButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), v);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.overflow_post_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {

                if (item.getItemId() == R.id.delete_post) {
                    postViewModel.deletePost(this.posts.get(position).post);
                    communityAdapter.notifyItemRemoved(position);
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }
}
