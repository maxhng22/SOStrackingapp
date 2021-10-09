package com.example.sostrackingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GPSActivty extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsactivty);

        findViewById(R.id.live).setOnClickListener(this);
        findViewById(R.id.gps).setOnClickListener(this);
        findViewById(R.id.indoor).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.live:
                i = new Intent(this, UserCurrentLocationActivity.class);
                startActivity(i);
                break;
            case R.id.gps:
                i = new Intent(this, TrackingActivity.class);
                startActivity(i);
                break;
            case R.id.indoor:
                //intent to ips system activity
                break;
        }
    }
}