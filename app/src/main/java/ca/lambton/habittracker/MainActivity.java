package ca.lambton.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;

import ca.lambton.habittracker.databinding.ActivityMainBinding;
import ca.lambton.habittracker.view.home.HomeFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    //DayScrollDatePicker mPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());


        Intent homeIntent = new Intent(this, HomeFragment.class);

        startActivity(homeIntent);

        //GridView gridView = binding.calendarWeek.calendarGridView;



        /*CalendarViewPercentage calendarViewPercentage = new CalendarViewPercentage(this);


        new ViewGroup.LayoutParams(200, 200);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(calendarViewPercentage);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(linearLayout, layoutParams);
        */

/*
        LocalDateTime myDateObj = LocalDateTime.now();


        DateTimeFormatter myDayObj = DateTimeFormatter.ofPattern("dd");
        DateTimeFormatter mymonthObj = DateTimeFormatter.ofPattern("MM");
        DateTimeFormatter myyearObj = DateTimeFormatter.ofPattern("yyyy");

        String formattedDay = myDateObj.format(myDayObj);
        String formattedMonth = myDateObj.format(mymonthObj);
        String formattedyear = myDateObj.format(myyearObj);


        //mPicker = findViewById(R.id.day_date_picker);
        //logic behind weekview
        final ThisLocalizedWeek usWeek = new ThisLocalizedWeek(Locale.US);
        System.out.println(usWeek);
        // The English (United States) week starts on SUNDAY and ends on SATURDAY
        System.out.println(usWeek.getFirstDay());
        System.out.println(usWeek.getLastDay());
        Log.d("TAG", "First day: " + usWeek.getFirstDay());
        //mPicker.setEndDate(day+7, month, year);
        String maindate = String.valueOf(usWeek.getFirstDay());
        int startdate = Integer.parseInt(maindate.substring(8, 10));
        Log.d("TAG", "First day: " + startdate);
        int startmonth = Integer.parseInt(maindate.substring(5, 7));
        Log.d("TAG", "First month: " + startmonth);
        int startyear = Integer.parseInt(maindate.substring(0, 4));
        Log.d("TAG", "First year: " + startyear);
        //mPicker.setStartDate(startdate,startmonth,startyear);

        String enddate = String.valueOf(usWeek.getLastDay());
        int endday = Integer.parseInt(enddate.substring(8, 10));
        Log.d("TAG", "endday: " + endday);
        int endmonth = Integer.parseInt(enddate.substring(5, 7));
        Log.d("TAG", "endmonth: " + endmonth);
        int endyear = Integer.parseInt(enddate.substring(0, 4));
        Log.d("TAG", "endyear: " + endyear);

        //mPicker.setEndDate(endday, endmonth, endyear);

        //logic behind weekview


*/





        /*mPicker.getSelectedDate(date -> {
            if(date != null){
                // do something with selected date

                Toast.makeText(MainActivity.this, "Data : "+ date.toString(), Toast.LENGTH_SHORT).show();
            }
        });*/


    }

}