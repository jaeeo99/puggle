package com.puggle.magic.puggle.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.puggle.magic.puggle.R;
import com.puggle.magic.puggle.service.ScreenService;

import java.util.List;

/**
 * Created by jaeeo99 on 2018. 2. 10..
 */

public class SettingsActivity extends PreferenceActivity {
    private static final String FLAG_TOP = "FLAG_TOP";
    private static final String FLAG_BOTTOM = "FLAG_BOTTOM";
    private static final String FLAG_LEFT = "FLAG_LEFT";
    private static final String FLAG_RIGHT = "FLAG_RIGHT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference myPref = findPreference("reset_pref");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove(FLAG_TOP);
                editor.remove(FLAG_TOP + "_ACTION");
                editor.remove(FLAG_BOTTOM);
                editor.remove(FLAG_BOTTOM + "_ACTION");
                editor.remove(FLAG_LEFT);
                editor.remove(FLAG_LEFT + "_ACTION");
                editor.remove(FLAG_RIGHT);
                editor.remove(FLAG_RIGHT + "_ACTION");
                editor.commit();
                return true;
            }
        });

        Intent intent = new Intent(this, ScreenService.class);
        startService(intent);
    }
}