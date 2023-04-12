package ca.lambton.habittracker.leaderboard.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentLeaderboardBinding;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModel;
import ca.lambton.habittracker.habit.viewmodel.HabitViewModelFactory;
import ca.lambton.habittracker.leaderboard.adapter.LeaderboardAdapter;
import ca.lambton.habittracker.leaderboard.model.Leaderboard;
import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardFragment extends Fragment {

    private FragmentLeaderboardBinding binding;
    private RecyclerView leaderboardRecycleView;
    private final List<Leaderboard> leaderboardList = new ArrayList<>();
    private LeaderboardAdapter leaderboardAdapter;
    private CircleImageView photoFirstPlace, photoSecondPlace, photoThirdPlace;
    private TextView nameFirstPlace, nameSecondPlace, nameThirdPlace;
    private HabitViewModel habitViewModel;
    private FirebaseUser mUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentLeaderboardBinding.inflate(LayoutInflater.from(requireContext()));
        leaderboardRecycleView = binding.leaderboardRecycleView;
        leaderboardRecycleView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        photoFirstPlace = binding.photoFirstPlace;
        nameFirstPlace = binding.nameFirstPlace;

        photoSecondPlace = binding.photoSecondPlace;
        nameSecondPlace = binding.nameSecondPlace;

        photoThirdPlace = binding.photoThirdPlace;
        nameThirdPlace = binding.nameThirdPlace;

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        habitViewModel = new ViewModelProvider(getViewModelStore(), new HabitViewModelFactory(requireActivity().getApplication())).get(HabitViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        leaderboardAdapter = new LeaderboardAdapter(leaderboardList, (cardView, position) -> {
            if (leaderboardList.get(position).getAccountId().equals(mUser.getUid())) {
                cardView.setCardBackgroundColor(requireActivity().getColor(R.color.yellow));
                cardView.setScaleY(1.15f);
                cardView.setScaleX(1.03f);
            } else {
                cardView.setScaleY(1.0f);
                cardView.setScaleX(1.0f);
            }
        });
        leaderboardRecycleView.setAdapter(leaderboardAdapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();
        leaderboardList.clear();
        habitViewModel.fetchAllLeaderboardInfo().observe(getViewLifecycleOwner(), leaderboards -> {

            List<Leaderboard> leaderboardOrdered = leaderboards.stream().sorted(Comparator.comparing(Leaderboard::getScore).reversed()).collect(Collectors.toList());

            leaderboardList.addAll(leaderboardOrdered);

            if (leaderboardList.size() == 1) {
                nameFirstPlace.setText(leaderboardOrdered.get(0).getName());
                if (!leaderboardList.get(0).getImageUrl().equals("")) {
                    Picasso.get().load(leaderboards.get(0).getImageUrl())
                            .placeholder(R.drawable.ic_profile_custom)
                            .fit()
                            .into(photoFirstPlace);
                }

            } else if (leaderboardList.size() == 2) {
                nameFirstPlace.setText(leaderboardOrdered.get(0).getName());
                nameSecondPlace.setText(leaderboardOrdered.get(1).getName());
                if (!leaderboardList.get(0).getImageUrl().equals("")) {
                    Picasso.get().load(leaderboards.get(0).getImageUrl())
                            .placeholder(R.drawable.ic_profile_custom)
                            .fit()
                            .into(photoFirstPlace);
                }


                if (!leaderboardList.get(1).getImageUrl().equals("")) {
                    Picasso.get().load(leaderboards.get(1).getImageUrl())
                            .placeholder(R.drawable.ic_profile_custom)
                            .fit()
                            .into(photoSecondPlace);
                }
            } else if (leaderboardList.size() >= 3) {
                nameFirstPlace.setText(leaderboardOrdered.get(0).getName());
                nameSecondPlace.setText(leaderboardOrdered.get(1).getName());
                nameThirdPlace.setText(leaderboardOrdered.get(2).getName());


                if (!leaderboardList.get(0).getImageUrl().equals("")) {
                    Picasso.get()
                            .load(leaderboardOrdered.get(0).getImageUrl())
                            .placeholder(R.drawable.ic_profile_custom)
                            .fit()
                            .into(photoFirstPlace);
                }

                if (!leaderboardList.get(1).getImageUrl().equals("")) {
                    Picasso.get().load(leaderboardOrdered.get(1).getImageUrl())
                            .placeholder(R.drawable.ic_profile_custom)
                            .fit()
                            .into(photoSecondPlace);
                }
                if (!leaderboardList.get(2).getImageUrl().equals("")) {
                    Picasso.get().load(leaderboardOrdered.get(2).getImageUrl())
                            .placeholder(R.drawable.ic_profile_custom)
                            .fit()
                            .into(photoThirdPlace);
                }

            }

            leaderboardAdapter.notifyDataSetChanged();

        });

    }
}
