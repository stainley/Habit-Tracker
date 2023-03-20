package ca.lambton.habittracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OnBoarding extends AppCompatActivity {

    private TextView tvSkip;
    private ViewPager viewPager;
    private LinearLayout layoutDots;
    private IntroPref introPref;
    private int[] layouts;
    private TextView[] dots;
    private MyViewPagerAdapter viewPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
    }
    introPref = new IntroPref(this);
        if (!introPref.isFirstTimeLaunch()) {
        launchHomeScreen();
        finish();
    }
        if (Build.VERSION.SDK_INT >= 21) {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    tvSkip = findViewById(R.id.tvSkip);
    viewPager = findViewById(R.id.viewPager);
    layoutDots = findViewById(R.id.layoutDots);

    layouts = new int[]{
        R.layout.intro_one,
                R.layout.intro_two,
                R.layout.intro_three,
                R.layout.intro_four
    };

    tvSkip.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int current = getItem(+1);
            if (current < layouts.length) {
                // move to next screen
                viewPager.setCurrentItem(current + 2);
            } else {
                launchHomeScreen();
            }
        }
    });
}