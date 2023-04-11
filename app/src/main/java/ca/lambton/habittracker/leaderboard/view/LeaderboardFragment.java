package ca.lambton.habittracker.leaderboard.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ca.lambton.habittracker.databinding.FragmentLeaderboardBinding;
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

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        leaderboardList.add(new Leaderboard("Arya Stark", 5000, ""));
        leaderboardList.add(new Leaderboard("John Smith", 2000, ""));
        leaderboardList.add(new Leaderboard("Sasha Stark", 8000, ""));


        List<Leaderboard> leaderBoardOrdered = leaderboardList.stream()
                .sorted(Comparator.comparing(Leaderboard::getScore).reversed())
                .collect(Collectors.toList());
        leaderboardAdapter = new LeaderboardAdapter(leaderBoardOrdered);
        leaderboardRecycleView.setAdapter(leaderboardAdapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Leaderboard> leaderBoardOrdered = leaderboardList.stream()
                .sorted(Comparator.comparing(Leaderboard::getScore).reversed())
                .collect(Collectors.toList());

        nameFirstPlace.setText(leaderBoardOrdered.get(0).getName());
        nameSecondPlace.setText(leaderBoardOrdered.get(1).getName());
        nameThirdPlace.setText(leaderBoardOrdered.get(2).getName());
    }

    @Override
    public void onStart() {
        super.onStart();


    }
}
