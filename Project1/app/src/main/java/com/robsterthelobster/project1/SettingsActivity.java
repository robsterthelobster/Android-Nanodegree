package com.robsterthelobster.project1;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

/**
 * Created by robin on 3/24/2016.
 */
public class SettingsActivity extends PreferenceActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }

}

