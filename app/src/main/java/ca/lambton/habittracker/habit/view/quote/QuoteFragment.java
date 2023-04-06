package ca.lambton.habittracker.habit.view.quote;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ca.lambton.habittracker.databinding.FragmentDayQuoteBinding;

public class QuoteFragment extends Fragment {

    FragmentDayQuoteBinding binding;
    private QuoteViewModel quoteViewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentDayQuoteBinding.inflate(LayoutInflater.from(requireContext()));

        quoteViewModel = new ViewModelProvider(requireActivity(), new QuoteViewModelFactory(requireActivity().getApplication())).get(QuoteViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        int randomQuote = (int) (Math.random() * 36);
        quoteViewModel.getQuote(randomQuote).observe(requireActivity(), quote -> {
            if (quote != null)
                binding.quoteDayMessageText.setText(quote.getDescription());
        });
    }
}
