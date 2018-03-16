package com.puggle.magic.puggle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.puggle.magic.puggle.R;
import com.puggle.magic.puggle.service.ScreenService;

import java.util.List;

/**
 * Created by jaeeo99 on 2018. 2. 10..
 */

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Intent intent = new Intent(this, ScreenService.class);
        startService(intent);
    }
}