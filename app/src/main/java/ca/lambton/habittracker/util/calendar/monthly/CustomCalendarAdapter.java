package ca.lambton.habittracker.util.calendar.monthly;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import ca.lambton.habittracker.R;

class CustomCalendarAdapter extends ArrayAdapter<Date> {
    private final CustomCalendarView customCalendarView;
    private HashSet<Date> eventDays;
    private ArrayList<String> habitProgress;
    private LayoutInflater inflater;

    public CustomCalendarAdapter(CustomCalendarView customCalendarView, Context context, ArrayList<Date> days, HashSet<Date> eventDays, ArrayList<String> habitProgress) {
        super(context, R.layout.control_calendar_day, days);
        this.customCalendarView = customCalendarView;
        this.eventDays = eventDays;
        this.habitProgress = habitProgress;
        inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // day in question
        Date date = getItem(position);
        int day = date.getDate();
        int month = date.getMonth();
        int year = date.getYear();
        Date today = new Date();

        if (view == null) {
            view = inflater.inflate(R.layout.control_calendar_day, parent, false);
        }

        if (eventDays != null) {
            for (int i = 0; i < eventDays.size(); i++) {
                Date eventDate = (Date) eventDays.toArray()[i];


                if (eventDate.getDate() == day && eventDate.getMonth() == month && eventDate.getYear() == year) {

                    view.findViewById(R.id.dateRelativeLayout).setBackground(customCalendarView.getResources().getDrawable(R.drawable.shape_circle));

                    if (this.habitProgress.get(i).equals("100")) {
                        view.findViewById(R.id.dateLinearLayout).setBackground(customCalendarView.getResources().getDrawable(R.drawable.shape_rec_round));
                    } else if (this.habitProgress.get(i).equals("50")) {
                        view.findViewById(R.id.dateLinearFirstHalf).setBackground(customCalendarView.getResources().getDrawable(R.drawable.shape_rec_left_round));
                    }

                    break;
                }
            }
        }

        ((TextView) view.findViewById(R.id.dateLabel)).setTypeface(null, Typeface.NORMAL);
        ((TextView) view.findViewById(R.id.dateLabel)).setTextColor(Color.BLACK);
        ((TextView) view.findViewById(R.id.dateLabel)).setText(String.valueOf(date.getDate()));

        return view;
    }
}
