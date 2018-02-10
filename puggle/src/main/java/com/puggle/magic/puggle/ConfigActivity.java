package com.puggle.magic.puggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

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