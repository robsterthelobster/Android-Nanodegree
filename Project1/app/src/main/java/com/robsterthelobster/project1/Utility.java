package com.robsterthelobster.project1;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
}
