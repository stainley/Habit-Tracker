package ca.lambton.habittracker.view.calendar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import ca.lambton.habittracker.util.CalendarDayView;
import ca.lambton.habittracker.util.DayData;

public class CalendarAdapter extends BaseAdapter {
    private Context mContext;
    private List<DayData> mDayDataList;

    public CalendarAdapter(Context context, List<DayData> dayDataList) {
        mContext = context;
        mDayDataList = dayDataList;
    }

    @Override
    public int getCount() {
        return mDayDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDayDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CalendarDayView calendarDayView;
        if (convertView == null) {
            calendarDayView = new CalendarDayView(mContext);
        } else {
            calendarDayView = (CalendarDayView) convertView;
        }

        DayData dayData = mDayDataList.get(position);
        calendarDayView.setPosition(position);
        calendarDayView.setDay(dayData.getDay());
        calendarDayView.setPercentage(dayData.getPercentage());
        calendarDayView.setDayNumber(dayData.getDayNumber());

        return calendarDayView;
    }
}
