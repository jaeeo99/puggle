package com.puggle.magic.puggle.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.puggle.magic.puggle.R;
import com.puggle.magic.puggle.service.ScreenService;

/**
 * Created by jaeeo99 on 2018. 2. 10..
 */

public class ConfigActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Intent intent = new Intent(this, ScreenService.class);
        startService(intent);
    }
}