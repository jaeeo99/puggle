package com.puggle.magic.puggle.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.puggle.magic.puggle.service.ScreenService;

public class PackageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_REPLACED)){
            Intent i = new Intent(context, ScreenService.class);
            context.startService(i);
        }
    }
}
