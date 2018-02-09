package com.puggle.magic.puggle;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.airbnb.lottie.Cancellable;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.mikepenz.iconics.context.IconicsLayoutInflater;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.github.controlwear.virtual.joystick.android.JoystickView;


public class LockScreenActivity extends AppCompatActivity {
    private boolean isCalled = false;
    private String callTo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        w.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_main);

        setDate();
        ArrayList<TextView> textViews = getTextViews();

        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.animationView);


        JoystickView joystick = findViewById(R.id.joystickView);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                for (TextView textView : textViews) {
                    textView.setTextColor(Color.BLACK);
                    textView.setTextSize(15);
                }
                Log.d("Log", "angle : " + angle + ", strength : " + strength);
                if(strength == 0 && angle == 0){
                    if(isCalled){
                        call(callTo);
                        isCalled = false;
                        callTo = "";
                    }
                    animationView.playAnimation();
                }
                else if (strength > 90 && strength < 101) {
                    animationView.pauseAnimation();
                    isCalled = false;
                    if (angle < 45) {
                        textViews.get(0).setTextColor(Color.WHITE);
                        textViews.get(0).setTextSize(20);
                        isCalled = true;
                        callTo = "010-3477-1507";
                    } else if (angle < 135) {
                        textViews.get(1).setTextColor(Color.WHITE);
                        textViews.get(1).setTextSize(20);
                        isCalled = true;
                        callTo = "010-3477-1507";
                    } else if (angle < 225) {
                        textViews.get(2).setTextColor(Color.WHITE);
                        textViews.get(2).setTextSize(20);
                        isCalled = true;
                        callTo = "010-3477-1507";
                    } else if (angle < 315) {
                        textViews.get(3).setTextColor(Color.WHITE);
                        textViews.get(3).setTextSize(20);
                        isCalled = true;
                        callTo = "010-3477-1507";
                    } else {
                        textViews.get(0).setTextColor(Color.WHITE);
                        textViews.get(0).setTextSize(20);
                        isCalled = true;
                        callTo = "010-3477-1507";
                    }
                } else {
                    animationView.pauseAnimation();
                    isCalled = false;
                }
            }
        }, 100);
    }

    public void setDate(){
        final Handler timeHandler = new Handler(getMainLooper());
        TextView timeTextView = findViewById(R.id.time);
        timeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timeTextView.setText(new SimpleDateFormat("h:mm", Locale.US).format(new Date()));
                timeHandler.postDelayed(this, 1000);
            }
        }, 10);
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("M월 d일 E요일", Locale.KOREAN);
        TextView dateTextView = findViewById(R.id.date);
        dateTextView.setText(dateFormat.format(today));
    }

    public ArrayList<TextView> getTextViews(){
        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add(findViewById(R.id.labelRight));
        textViews.add(findViewById(R.id.labelTop));
        textViews.add(findViewById(R.id.labelLeft));
        textViews.add(findViewById(R.id.labelBottom));
        return textViews;
    }

    /** Called when the user taps the Send button */
    public void call(String phoneNum) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionResult = checkSelfPermission(Manifest.permission.CALL_PHONE);

            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(LockScreenActivity.this);
                    dialog.setTitle("권한이 필요합니다.")
                            .setMessage("이 기능을 사용하기 위해서는 단말기의 \"전화걸기\" 권한이 필요합니다. 계속 하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                                    }
                                }
                            })
                            .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(LockScreenActivity.this, "기능을 취소했습니다", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create()
                            .show();
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                }
            } else {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
            startActivity(intent);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:010-1111-2222"));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                }
            } else {
                Toast.makeText(LockScreenActivity.this, "권한요청을 거부했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
