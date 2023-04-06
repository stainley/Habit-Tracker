package ca.lambton.habittracker.util.calendar.weekly;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Random;

import ca.lambton.habittracker.R;

public class CalendarViewPercentage extends LinearLayout {

    private final int NUM_DAYS = 6;

    public CalendarViewPercentage(Context context) {
        super(context);

        setOrientation(HORIZONTAL);
        for (int i = 0; i < NUM_DAYS; i++) {
            View dayView = LayoutInflater.from(context).inflate(R.layout.calendar_day_view, this, false);
            TextView dayOfWeekTextView = dayView.findViewById(R.id.dayOfWeekTextView);
            dayOfWeekTextView.setText(getDayOfWeek(i));


            CircularProgressIndicator progressIndicator = dayView.findViewById(R.id.progressRadiusProgressBar);
            /*progressIndicator.setIndicatorDirection(CircularProgressIndicator.INDICATOR_DIRECTION_COUNTERCLOCKWISE);*/
            Random r = new Random();
            int low = 0;
            int high = 100;
            int randomValue = r.nextInt(high - low) + low;
            progressIndicator.setProgress(randomValue);
            addView(dayView);
        }
    }

    private String getDayOfWeek(int dayIndex) {
        String[] daysOfWeeks = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        return daysOfWeeks[dayIndex];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int totalWidth = 0;
        int totalHeight = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            totalWidth += child.getMeasuredWidth();
            totalHeight = Math.max(totalHeight, child.getMeasuredHeight());
        }

        setMeasuredDimension(totalWidth, totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            child.layout(left, 0, left + childWidth, childHeight);

            left += childWidth;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            int centerX = child.getLeft() + (child.getWidth() / 2);
            int centerY = child.getTop() + (child.getHeight() / 2);

            ProgressBar progressRadiusProgressBar = child.findViewById(R.id.progressRadiusProgressBar);
            int radius = progressRadiusProgressBar.getProgress() * child.getWidth() / 2 / 100;

            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.md_theme_dark_onError));

            canvas.drawCircle(centerX, centerY, radius, paint);
        }
    }

}
