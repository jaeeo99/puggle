package com.puggle.magic.puggle.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.puggle.magic.puggle.R;
import com.puggle.magic.puggle.fragment.SelectDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.github.controlwear.virtual.joystick.android.JoystickView;


public class LockScreenActivity extends Activity {
    private static final String FLAG_TOP = "FLAG_TOP";
    private static final String FLAG_BOTTOM = "FLAG_BOTTOM";
    private static final String FLAG_LEFT = "FLAG_LEFT";
    private static final String FLAG_RIGHT = "FLAG_RIGHT";
    private static final String FLAG_NONE = "FLAG_NONE";

    private String flag = FLAG_NONE;

    private KeyguardManager km = null;
    private KeyguardManager.KeyguardLock keyLock = null;

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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if ( keyCode == KeyEvent.KEYCODE_BACK ) {
                return true;
            }
            else if ( keyCode == KeyEvent.KEYCODE_HOME ) {
                return true;
            }
            else if ( keyCode == KeyEvent.KEYCODE_MENU ) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            Cursor cursor = getContentResolver().query(data.getData(),
                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
            cursor.moveToFirst();
            String name = cursor.getString(0);  //0은 이름을 얻어옵니다.
            String number = cursor.getString(1);   //1은 번호를 받아옵니다.
            Log.d("name", name);
            Log.d("number", number);
            cursor.close();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        w.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.activity_lockscreen);

        setDateTime();
        ArrayList<TextView> mTextViews = getTextViews();

        JoystickView joystick = findViewById(R.id.joystickView);
        setAnimationWithTouchEvent(joystick);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                for (TextView textView : mTextViews) {
                    textView.setTextColor(Color.BLACK);
                    textView.setTextSize(15);
                    textView.setVisibility(View.GONE);
                }
                if(strength == 0 && angle == 0){
                    if(!flag.equals(FLAG_NONE)){
                        raiseAction();
//                        switch(flag) {
//                            case FLAG_TOP:
////                                requestUnlock();
//                                return;
//                            case FLAG_BOTTOM:
//                                return;
//                            case FLAG_LEFT:
//                                return;
//                            case FLAG_RIGHT:
//                                return;
//                            default:
//                                return;
//                        }
                    }
                }
                else if (strength > 90 && strength < 101) {
                    flag = FLAG_NONE;
                    if (angle < 45) {
                        mTextViews.get(0).setTextColor(Color.WHITE);
                        mTextViews.get(0).setTextSize(20);
                        mTextViews.get(0).setVisibility(View.VISIBLE);
                        flag = FLAG_RIGHT;
                    } else if (angle < 135) {
                        mTextViews.get(1).setTextColor(Color.WHITE);
                        mTextViews.get(1).setTextSize(20);
                        mTextViews.get(1).setVisibility(View.VISIBLE);
                        flag = FLAG_TOP;
                    } else if (angle < 225) {
                        mTextViews.get(2).setTextColor(Color.WHITE);
                        mTextViews.get(2).setTextSize(20);
                        mTextViews.get(2).setVisibility(View.VISIBLE);
                        flag = FLAG_LEFT;
                    } else if (angle < 315) {
                        mTextViews.get(3).setTextColor(Color.WHITE);
                        mTextViews.get(3).setTextSize(20);
                        mTextViews.get(3).setVisibility(View.VISIBLE);
                        flag = FLAG_BOTTOM;
                    } else {
                        mTextViews.get(0).setTextColor(Color.WHITE);
                        mTextViews.get(0).setTextSize(20);
                        mTextViews.get(0).setVisibility(View.VISIBLE);
                        flag = FLAG_RIGHT;
                    }
                } else {
                    flag = FLAG_NONE;
                }
            }
        }, 100);
    }

    private void raiseAction(){
        SharedPreferences sharedPref = getSharedPreferences("puggle", Context.MODE_PRIVATE);
        int actionNum = sharedPref.getInt(flag, -1);
        String actionName = sharedPref.getString(flag + "_ACTION", "");

        if (actionNum == -1) {
            DialogFragment newFragment = new SelectDialogFragment();
            newFragment.show(getFragmentManager(), "puggle");
        }

        Log.d("flag", flag);
        Log.d("actionNum", "" + actionNum);
        Log.d("actionName", actionName);
    }

    private void setDateTime(){
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

    public void setAnimationWithTouchEvent(JoystickView joystickView) {
        LottieAnimationView mWaitingAnimationView = (LottieAnimationView) findViewById(R.id.waitingAnimationView);
        LottieAnimationView mTouchedAnimationView  = (LottieAnimationView) findViewById(R.id.touchedAnimationView);
        ImageView layoutBackground = (ImageView) findViewById(R.id.layoutBackground);
        layoutBackground.setImageAlpha(200);

        joystickView.setOnTouchListener(new JoystickView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    mTouchedAnimationView.setVisibility(View.VISIBLE);
                    mTouchedAnimationView.playAnimation();
                    mWaitingAnimationView.cancelAnimation();
                    mWaitingAnimationView.setVisibility(View.GONE);
                    layoutBackground.setImageAlpha(100);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    mWaitingAnimationView.setVisibility(View.VISIBLE);
                    mWaitingAnimationView.playAnimation();
                    mTouchedAnimationView.cancelAnimation();
                    mTouchedAnimationView.setVisibility(View.GONE);
                    layoutBackground.setImageAlpha(200);
                }
                return false;
            }
        });
    }

    private ArrayList<TextView> getTextViews(){
        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add(findViewById(R.id.labelRight));
        textViews.add(findViewById(R.id.labelTop));
        textViews.add(findViewById(R.id.labelLeft));
        textViews.add(findViewById(R.id.labelBottom));
        return textViews;
    }

    private void getPreferences(){
        SharedPreferences sharedPref = getBaseContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    private void requestUnlock(){
        if (km == null) {
            km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        }
        if (keyLock == null) {
            keyLock = km.newKeyguardLock(Context.KEYGUARD_SERVICE);
        }
        keyLock.disableKeyguard();
        finish();
    }

    private void requestCall(String phoneNum) {
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

    private void requesContact(int requestCode){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }
}
