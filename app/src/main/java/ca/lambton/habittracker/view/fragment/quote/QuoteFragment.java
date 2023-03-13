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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;
import java.util.Random;

import ca.lambton.habittracker.databinding.FragmentDayQuoteBinding;
import ca.lambton.habittracker.model.Quote;

public class QuoteFragment extends Fragment {

    FragmentDayQuoteBinding binding;
    private QuoteViewModel quoteViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentDayQuoteBinding.inflate(inflater);

        CardView quoteDayCard = binding.quoteDayCard;
        quoteDayCard.setOnClickListener(this::changeCardQuote);
        quoteViewModel = new ViewModelProvider(requireActivity(), new QuoteViewModelFactory(requireActivity().getApplication())).get(QuoteViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int randomQuote = (int) (Math.random() * 36);
        quoteViewModel.getQuote(randomQuote).observe(requireActivity(), quote -> {
            if (quote != null)
                binding.quoteDayMessageText.setText(quote.getDescription());
        });


    }

    private void changeCardQuote(View view) {

    }
}
