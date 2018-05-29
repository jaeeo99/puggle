package com.puggle.magic.puggle.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.puggle.magic.puggle.FingerprintHandler;
import com.puggle.magic.puggle.R;
import com.puggle.magic.puggle.fragment.SelectDialogFragment;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import io.github.controlwear.virtual.joystick.android.JoystickView;


public class LockScreenActivity extends Activity {
    private static final String FLAG_TOP = "FLAG_TOP";
    private static final String FLAG_BOTTOM = "FLAG_BOTTOM";
    private static final String FLAG_LEFT = "FLAG_LEFT";
    private static final String FLAG_RIGHT = "FLAG_RIGHT";
    private static final String FLAG_NONE = "FLAG_NONE";
    private static final String KEY_NAME = "PUGGLE";

    private String flag = FLAG_NONE;
    private ArrayList<TextView> mTextViews;

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyguardManager.KeyguardLock keyLock = null;

    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        w.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        setContentView(R.layout.activity_lockscreen);

        fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        mTextViews = getTextViews();

        setDateTime();

        JoystickView joystick = findViewById(R.id.joystickView);
        setAnimationWithTouchEvent(joystick);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if(strength == 0 && angle == 0){
                    if(!flag.equals(FLAG_NONE)){
                        raiseAction();
                    }
                }
                else if (strength > 90 && strength < 101) {
                    flag = FLAG_NONE;
                    if (angle < 45) {
                        flag = FLAG_RIGHT;
                    } else if (angle < 135) {
                        flag = FLAG_TOP;
                    } else if (angle < 225) {
                        flag = FLAG_LEFT;
                    } else if (angle < 315) {
                        flag = FLAG_BOTTOM;
                    } else {
                        flag = FLAG_RIGHT;
                    }
                } else {
                    flag = FLAG_NONE;
                }
            }
        }, 100);

        if (!keyguardManager.isKeyguardSecure()) {
            Toast.makeText(this,
                    "Lock screen security not enabled in Settings",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,
                    "Fingerprint authentication permission not enabled",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {
            // This happens when no fingerprints are registered.
            Toast.makeText(this,
                    "Register at least one fingerprint in Settings",
                    Toast.LENGTH_LONG).show();
            return;
        }

        generateKey();

        if (cipherInit()) {
            cryptoObject = new FingerprintManager.CryptoObject(cipher);
            FingerprintHandler helper = new FingerprintHandler(this);
            helper.startAuth(fingerprintManager, cryptoObject);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            Cursor cursor = getContentResolver().query(data.getData(),
                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
            cursor.moveToFirst();
            String name = cursor.getString(0);  //0은 이름을 얻어옵니다.
            String number = cursor.getString(1);   //1은 번호를 받아옵니다.
            cursor.close();

            SharedPreferences sharedPref = getBaseContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(flag, 0);
            editor.putString(flag + "_ACTION", name);
            editor.putString(flag + "_NUMBER", number);
            editor.commit();
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException e) {
            throw new RuntimeException(
                    "Failed to get KeyGenerator instance", e);
        }


        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private void raiseAction(){
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        int actionNum = sharedPref.getInt(flag, -1);
        String actionName = sharedPref.getString(flag + "_ACTION", "");
        String phoneNum = sharedPref.getString(flag + "_NUMBER", "");

        switch (actionNum) {
            case -1:
                DialogFragment newFragment = new SelectDialogFragment();
                newFragment.show(getFragmentManager(), "puggle");
                break;
            case 0:
                requestCall(phoneNum);
                break;
            case 1:
                requestUnlock();
                break;
        }

        Log.d("flag", flag);
        Log.d("actionNum", "" + actionNum);
        Log.d("actionName", actionName);
    }

    private void setDateTime(){
        final Handler timeHandler = new Handler(getMainLooper());
        TextView dateTextView = findViewById(R.id.date);
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
                    Animation fadeIn = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_in);

                    for (TextView textView : mTextViews) {
                        textView.setVisibility(View.VISIBLE);
                        textView.startAnimation(fadeIn);
                    }
                    layoutBackground.setImageAlpha(100);
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    mWaitingAnimationView.setVisibility(View.VISIBLE);
                    mWaitingAnimationView.playAnimation();
                    mTouchedAnimationView.cancelAnimation();
                    mTouchedAnimationView.setVisibility(View.GONE);
                    Animation fadeOut = AnimationUtils.loadAnimation(getBaseContext(), R.anim.fade_out);

                    for (TextView textView : mTextViews) {
                        textView.setVisibility(View.GONE);
                        textView.startAnimation(fadeOut);
                    }
                    layoutBackground.setImageAlpha(200);
                }
                return false;
            }
        });
    }

    private ArrayList<TextView> getTextViews(){
        ArrayList<TextView> textViews = new ArrayList<>();
        TextView labelRight = findViewById(R.id.labelRight);
        TextView labelTop = findViewById(R.id.labelTop);
        TextView labelLeft = findViewById(R.id.labelLeft);
        TextView labelBottom = findViewById(R.id.labelBottom);
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        labelRight.setText(sharedPref.getString(FLAG_RIGHT + "_ACTION", "+"));
        labelTop.setText(sharedPref.getString(FLAG_TOP + "_ACTION", "+"));
        labelLeft.setText(sharedPref.getString(FLAG_LEFT + "_ACTION", "+"));
        labelBottom.setText(sharedPref.getString(FLAG_BOTTOM + "_ACTION", "+"));
        textViews.add(labelRight);
        textViews.add(labelTop);
        textViews.add(labelLeft);
        textViews.add(labelBottom);
        return textViews;
    }

    private void getPreferences(){
        SharedPreferences sharedPref = getBaseContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    public void requestUnlock(){
        if (keyLock == null) {
            keyLock = keyguardManager.newKeyguardLock(Context.KEYGUARD_SERVICE);
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

    public void requestContact(int requestCode){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }
    public String getFlag(){
        return flag;
    }
}
