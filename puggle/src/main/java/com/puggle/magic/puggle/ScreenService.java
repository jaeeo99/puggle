package com.puggle.magic.puggle;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ScreenService extends Service {
    private ScreenReceiver mScreenReceiver = null;
    private PackageReceiver mPackageReceiver;

    public ScreenService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("ScreenService", "onCreate");
        super.onCreate();
        mScreenReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenReceiver, filter);


        mPackageReceiver = new PackageReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        registerReceiver(mPackageReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        Log.d("ScreenService", "onStartCommand");
        if(intent != null){
            if(intent.getAction() == null){
                if(mScreenReceiver == null){
                    mScreenReceiver = new ScreenReceiver();
                    IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
                    registerReceiver(mScreenReceiver, filter);
                }
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy(){
        Log.d("ScreenService", "onDestroy");
        super.onDestroy();
        if (mScreenReceiver != null) {
            unregisterReceiver(mScreenReceiver);
        }
        if (mPackageReceiver != null) {
            unregisterReceiver(mPackageReceiver);
        }
    }
}
