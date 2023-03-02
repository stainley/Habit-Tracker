package ca.lambton.habittracker.view.home;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import ca.lambton.habittracker.R;
import ca.lambton.habittracker.databinding.FragmentHomeBinding;
import ca.lambton.habittracker.view.fragment.calendar.ProgressCalendarFragment;
import ca.lambton.habittracker.view.fragment.quote.QuoteFragment;

public class HomeFragment extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ca.lambton.habittracker.databinding.FragmentHomeBinding binding = FragmentHomeBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());


        FragmentManager supportFragmentManager = getSupportFragmentManager();

        /*Fragment calendarFragment = new ProgressCalendarFragment();
        supportFragmentManager.beginTransaction().replace(R.id.homeCalendarView, calendarFragment).commit();
*/

        Fragment quoteDayFragment = new QuoteFragment();
        supportFragmentManager.beginTransaction().replace(R.id.quoteDayFragmentView, quoteDayFragment).commit();
    }


}
