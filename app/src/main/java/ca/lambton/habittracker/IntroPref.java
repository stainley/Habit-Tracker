package ca.lambton.habittracker;

import android.content.Context;
import android.content.SharedPreferences;

public class IntroPref {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;

    private static final String PREF_NAME = "xyz";
    private static final String IS_FIRST_TIME_LAUNCH = "firstTime";

}
