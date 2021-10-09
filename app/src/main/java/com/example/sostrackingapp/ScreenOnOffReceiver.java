package com.example.sostrackingapp;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ScreenOnOffReceiver extends BroadcastReceiver {

    public final static String SCREEN_TOGGLE_TAG = "SCREEN_TOGGLE_TAG";
    public static int count = 0;
    public static int volume_count = 0;
    private static boolean onCountingStutus = false;
    private SharedPreferences mPrefs;
    private String quick_call;
    private boolean police_setting_status = false;
    private boolean hospital_setting_status = false;
    private boolean fakeCall = true;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Log.i("count now is", "clear");
            onCountingStutus = true;
            setCountToZero();
            return false;
        }
    });


    private Handler volume_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Log.i("count now is", "clear");
            setVolumeCountToZero();
            return false;
        }
    });


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        mPrefs = context.getSharedPreferences("com.example.sostrackingapp", MODE_PRIVATE);
        quick_call = mPrefs.getString("quick_call", "disable");
        police_setting_status = mPrefs.getBoolean("police", false);
        hospital_setting_status = mPrefs.getBoolean("hospital", false);

//
//        PackageManager pm = context.getPackageManager();
//        Intent launchIntent = pm.getLaunchIntentForPackage("com.example.sostrackingapp");
//        context.startActivity(launchIntent);


        try {

            if (Intent.ACTION_SCREEN_OFF.equals(action) || (Intent.ACTION_SCREEN_ON.equals(action))) {
                Log.d(SCREEN_TOGGLE_TAG, "Screen is turn off.");
                if (quick_call.equals("enable")) {
                    count++;
                    if (count == 3) {
                        if (hospital_setting_status) {
                            Handler handler2 = new Handler();
                            final Runnable r = new Runnable() {
                                public void run() {
                                    if (count == 3) {
                                        phoneCall("tel:01110082709", context);
                                    }
                                }
                            };
                            handler2.postDelayed(r, 500);
                        }

                    } else if (count == 5) {
                        if (police_setting_status) {
                            Handler handler2 = new Handler();
                            final Runnable r = new Runnable() {
                                public void run() {
                                    if (count == 5) {
                                        phoneCall("tel:01110082709", context);
                                    }
                                }
                            };
                            handler2.postDelayed(r, 500);
                        }

                    }

                    handler.removeMessages(1);
                    handler.sendEmptyMessageDelayed(1, 3000);
                }

            } else {
                if (!FakeCallRinging.active) {
                    volume_count++;
                    Log.d(SCREEN_TOGGLE_TAG, "Screen is turn off." + volume_count);
                    if (volume_count >= 4) {
                        volume_count = 0;

                        if (fakeCall) {
                            Log.d(SCREEN_TOGGLE_TAG, "start now is turn off." + volume_count);
                            Intent dialogIntent = new Intent(context, FakeCallRinging.class);
                            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(dialogIntent);
                        }


                        fakeCall = false;

                        Handler handler3 = new Handler();
                        final Runnable r = new Runnable() {
                            public void run() {
                                fakeCall = true;
                            }
                        };
                        handler3.postDelayed(r, 3000);


                    }

                    volume_handler.removeMessages(1);
                    volume_handler.sendEmptyMessageDelayed(1, 3000);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void phoneCall(String phone_num, Context context) {

        Uri callUri = Uri.parse(phone_num);
        Intent callIntent = new Intent(Intent.ACTION_CALL, callUri);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        context.startActivity(callIntent);
    }

    public void setCountToZero() {
        onCountingStutus = false;
        count = 0;
    }

    private void setVolumeCountToZero() {
        volume_count = 0;
    }


}