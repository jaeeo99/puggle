package com.puggle.magic.puggle;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.util.Log;
import android.widget.Toast;

import com.puggle.magic.puggle.activity.LockScreenActivity;

/**
 * Created by jaeeo99 on 2018. 3. 20..
 */

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context appContext;

    public FingerprintHandler(Context context) {
        this.appContext = context;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        Log.d("FingerpintHandler", "startAuth");
        CancellationSignal cancellationSignal = new CancellationSignal();
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errMsgId,
                                      CharSequence errString) {
        Log.d("FingerpintHandler", "onAuthenticationError");
        Toast.makeText(appContext,
                "Authentication error\n" + errString,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId,
                                     CharSequence helpString) {
        Log.d("FingerpintHandler", "onAuthenticationHelp");
        Toast.makeText(appContext,
                "Authentication help\n" + helpString,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationFailed() {
        Log.d("FingerpintHandler", "onAuthenticationFailed");
        Toast.makeText(appContext,
                "등록되지 않은 지문입니다." ,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {
        Log.d("FingerpintHandler", "onAuthenticationSucceeded");
        ((LockScreenActivity)appContext).requestUnlock();
        Toast.makeText(appContext,
                "Authentication success",
                Toast.LENGTH_LONG).show();
    }
}