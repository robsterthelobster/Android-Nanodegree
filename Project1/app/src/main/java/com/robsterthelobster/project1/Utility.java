package com.robsterthelobster.project1;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by robin on 3/26/2016.
 */
public class Utility {

    // get sorting type
    // popular is the default sorting method
    public static String getSortType(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sort = prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_popular));
        return sort;
    }

    public static String getRatingStr(String rating) {
        return rating + "/10";
    }

    // day month year
    // api format is yyyy-mm-dd
    public static String formatDateDMY(String str){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(str);
            return DateFormat.getDateInstance().format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}
