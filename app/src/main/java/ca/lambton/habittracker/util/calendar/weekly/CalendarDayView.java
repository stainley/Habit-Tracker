package ca.lambton.habittracker.util.calendar.weekly;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import ca.lambton.habittracker.R;

public class CalendarDayView extends FrameLayout {
    private TextView mDayTextView;
    private CircularProgressIndicator mProgressBar;

    private TextView dayNumberTextView;
    private int position;

    public CalendarDayView(Context context) {
        super(context);
        init();
    }

    public CalendarDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalendarDayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.calendar_day_view, this, true);
        mDayTextView = findViewById(R.id.dayOfWeekTextView);
        mDayTextView.setTextColor(Color.LTGRAY);
        mProgressBar = findViewById(R.id.progressRadiusProgressBar);
        dayNumberTextView = findViewById(R.id.dayNumber);
        dayNumberTextView.setTextColor(Color.LTGRAY);
        mProgressBar.setMax(100);
    }

    public void setDay(String day) {
        if (position == 0) {
            mDayTextView.setTextColor(Color.BLACK);
        }
        mDayTextView.setText(day);
    }

    public void setPercentage(int percentage) {
        mProgressBar.setProgress(percentage);
    }

    public void setDayNumber(int dayNumber) {
        if (position == 0) {
            dayNumberTextView.setTextColor(Color.BLACK);
        }
        dayNumberTextView.setText(String.valueOf(dayNumber));
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
