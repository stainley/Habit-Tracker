package ca.lambton.habittracker.leaderboard.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ca.lambton.habittracker.databinding.FragmentLeaderboardBinding;
import ca.lambton.habittracker.leaderboard.adapter.LeaderboardAdapter;
import ca.lambton.habittracker.leaderboard.model.Leaderboard;

public class LeaderboardFragment extends Fragment {

    private FragmentLeaderboardBinding binding;
    private RecyclerView leaderboardRecycleView;
    private final List<Leaderboard> leaderboardList = new ArrayList<>();
    private LeaderboardAdapter leaderboardAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentLeaderboardBinding.inflate(LayoutInflater.from(requireContext()));
        leaderboardRecycleView = binding.leaderboardRecycleView;
        leaderboardRecycleView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        leaderboardList.add(new Leaderboard("Arya Stark", 5000, ""));
        leaderboardList.add(new Leaderboard("John Smith", 2000, ""));
        leaderboardList.add(new Leaderboard("Sasha Stark", 8000, ""));

        leaderboardAdapter = new LeaderboardAdapter(leaderboardList);
        leaderboardRecycleView.setAdapter(leaderboardAdapter);

        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();


    }
}
