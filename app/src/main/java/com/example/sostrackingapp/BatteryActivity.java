package com.example.sostrackingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BatteryActivity extends AppCompatActivity {

    private TextView statusLabel;
    private TextView percentageLabel;
    private ImageView batteryImage;
    private FloatingActionButton setting;

    private final BatteryReceiver mBatteryReceiver = new BatteryReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            String statusLabel = b.getString("statusLabel");
            int percentage = b.getInt("percentage", 0);
            setStatusLabel(statusLabel);
            setBattery(percentage);
        }
    };
    private final IntentFilter mIntentFilter = new IntentFilter("Intent.ACTION_BATTERY_CHANGED");

    private void setStatusLabel(String status) {
        statusLabel.setText(status);
    }

    private void setBattery(int percent) {
        percentageLabel.setText(percent + "%");
        Resources res = getResources();
        if (percent >= 90) {
            batteryImage.setImageDrawable(res.getDrawable(R.drawable.b100));
        } else if (90 > percent && percent >= 65) {
            batteryImage.setImageDrawable(res.getDrawable(R.drawable.b75));
        } else if (65 > percent && percent >= 36) {
            batteryImage.setImageDrawable(res.getDrawable(R.drawable.b50));
        } else {
            batteryImage.setImageDrawable(res.getDrawable(R.drawable.b0));
//            this.showNotification(context);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        statusLabel = findViewById(R.id.statusLabel);
        percentageLabel = findViewById(R.id.percentageLabel);
        batteryImage = findViewById(R.id.batteryImage);

        if (mBatteryReceiver != null) {
            setBattery(BatteryReceiver.getPercentage());
            setStatusLabel(BatteryReceiver.getStatusLabel());
        }


        setting = findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BatteryActivity.this, BatterySettingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBatteryReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mBatteryReceiver);
        super.onPause();
    }
}