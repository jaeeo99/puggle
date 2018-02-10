package com.puggle.magic.puggle;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            ComponentName componentName = new ComponentName(context.getPackageName(), ScreenService.class.getName());
            ComponentName serviceComponentName = context.startService(new Intent().setComponent(componentName));
            if (serviceComponentName == null){
                Log.e("BootReceiver", "Could not start service " + componentName.toString());
            }
            Intent i = new Intent(context, LockScreenActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
