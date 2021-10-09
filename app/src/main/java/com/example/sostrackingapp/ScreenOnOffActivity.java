package com.example.sostrackingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;


public class ScreenOnOffActivity extends AppCompatActivity {

    private ScreenOnOffReceiver screenOnOffReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);



        Intent backgroundService = new Intent(getApplicationContext(), ScreenOnOffBackgroundService.class);
        startService(backgroundService);



        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(intent.hasExtra("test")) {
            Uri callUri = Uri.parse("tel:0182248892 ");
            Intent callIntent = new Intent(Intent.ACTION_CALL, callUri);
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            startActivity(callIntent);
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(ScreenOnOffReceiver.SCREEN_TOGGLE_TAG, "Activity onDestroy");
    }
}