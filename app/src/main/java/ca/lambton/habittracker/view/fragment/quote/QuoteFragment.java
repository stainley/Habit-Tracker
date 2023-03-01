package ca.lambton.habittracker.view.fragment.quote;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import ca.lambton.habittracker.databinding.FragmentDayQuoteBinding;

public class QuoteFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FragmentDayQuoteBinding binding = FragmentDayQuoteBinding.inflate(inflater);

        CardView quoteDayCard = binding.quoteDayCard;
        quoteDayCard.setOnClickListener(this::changeCardQuote);

        return binding.getRoot();
    }

    private void changeCardQuote(View view) {
        Toast.makeText(getContext(), "Show a new card message on double tap", Toast.LENGTH_LONG).show();
    }
}