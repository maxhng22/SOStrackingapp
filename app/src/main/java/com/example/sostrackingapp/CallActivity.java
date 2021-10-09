package com.example.sostrackingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CallActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    EditText number;
    private SharedPreferences mPrefs;

    private final String pref_duration = "timer_duration";
    private final String pref_timer = "timer";

    private boolean timer;
    private int timer_duration;
    private Intent intent;

    private FloatingActionButton setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        setTitle("Fake Call");


        mPrefs = getSharedPreferences("com.example.sostrackingapp", MODE_PRIVATE);
        setSharedPref();

        Button call = (Button) findViewById(R.id.BtnCall);

        call.setOnClickListener(v -> {
            if (timer) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fakeCallIntent();
                    }
                }, timer_duration);
            } else {
                fakeCallIntent();
            }

        });


        setting = findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(CallActivity.this, FakeCallSettingActivity.class);
                startActivity(intent);
            }
        });

    }

    private void fakeCallIntent() {
        Intent intent = new Intent(CallActivity.this, FakeCallRinging.class);
        startActivity(intent);
    }


    private void setSharedPref(){
        timer = mPrefs.getBoolean(pref_timer, false);
        timer_duration = mPrefs.getInt(pref_duration, 30000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSharedPref();
    }


}