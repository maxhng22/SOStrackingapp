package com.example.sostrackingapp;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
//import android.support.annotation.Nullable;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.sostrackingapp.ScreenOnOffReceiver;


public class ScreenOnOffBackgroundService extends Service {

    private ScreenOnOffReceiver screenOnOffReceiver = null;

    private BatteryReceiver mBatteryReceiver =null;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Create an IntentFilter instance.
        IntentFilter intentFilter = new IntentFilter();

        // Add network connectivity change action.
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        // Set broadcast receiver priority.
        intentFilter.setPriority(100);


        mBatteryReceiver=new BatteryReceiver();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.intent.action.BATTERY_CHANGED");

        // Create a network change broadcast receiver.
        screenOnOffReceiver = new ScreenOnOffReceiver();

        // Register the broadcast receiver with the intent filter object.
        registerReceiver(screenOnOffReceiver, intentFilter);
        registerReceiver(mBatteryReceiver, mIntentFilter);
        Log.d(ScreenOnOffReceiver.SCREEN_TOGGLE_TAG, "Service onCreate: screenOnOffReceiver is registered.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister screenOnOffReceiver when destroy.
        if(screenOnOffReceiver!=null)
        {
            unregisterReceiver(screenOnOffReceiver);
            unregisterReceiver(mBatteryReceiver);
            Log.d(ScreenOnOffReceiver.SCREEN_TOGGLE_TAG, "Service onDestroy: screenOnOffReceiver is unregistered.");
        }
    }
}