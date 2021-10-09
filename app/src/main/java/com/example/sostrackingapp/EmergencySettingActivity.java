package com.example.sostrackingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

public class EmergencySettingActivity extends AppCompatActivity implements View.OnClickListener {


    private SharedPreferences mPrefs;
    private boolean police_setting_status = false;
    private boolean hospital_setting_status = false;
    private String quick_call;
    RadioGroup rGroup;
    private SwitchCompat switch_police;
    private SwitchCompat switch_hospital;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_setting_activty);


        //get shared preference
        mPrefs = getSharedPreferences("com.example.sostrackingapp", MODE_PRIVATE);
        quick_call = mPrefs.getString("quick_call", "disable");
        police_setting_status = mPrefs.getBoolean("police", false);
        hospital_setting_status = mPrefs.getBoolean("hospital", false);

        //set id
        rGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        switch_police = (SwitchCompat) findViewById(R.id.police);
        switch_hospital = (SwitchCompat) findViewById(R.id.hospital);

        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.reset).setOnClickListener(this);

        //set switch status
        switch_police.setChecked(police_setting_status);
        switch_hospital.setChecked(hospital_setting_status);

        //set radio button status
        RadioButton radioButton;
        if (quick_call.equals("disable")) {
            radioButton = (RadioButton) findViewById(R.id.radio1);
        } else {
            radioButton = (RadioButton) findViewById(R.id.radio0);
            switch_police.setClickable(true);
            switch_hospital.setClickable(true);
        }
        radioButton.setChecked(true);

        //create onChecked listener for radio group
        rGroup.setOnCheckedChangeListener((group, checkedId) -> {
            //set quick_call
            if (checkedId == R.id.radio0) {
                quick_call="enable";
                switch_police.setClickable(true);
                switch_hospital.setClickable(true);
            } else if (checkedId == R.id.radio1) {
                quick_call="disable";
                switch_police.setChecked(false);
                switch_hospital.setChecked(false);
                switch_police.setClickable(false);
                switch_hospital.setClickable(false);
                police_setting_status=false;
                hospital_setting_status=false;
            }
        });

        //switch on checked listener
        switch_police.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                police_setting_status = isChecked;
            }
        });

        //switch on checked listener
        switch_hospital.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hospital_setting_status = isChecked;
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.reset) {
            String string_audio_file = "quick_call";
            RadioButton radioButton = (RadioButton) findViewById(R.id.radio1);
            radioButton.setChecked(true);
            mPrefs.edit().putString(string_audio_file, "disable").commit();

            String police = "police";
            switch_police.setChecked(false);
            switch_police.setClickable(false);
            mPrefs.edit().putBoolean(police, false).commit();

            String hospital = "hospital";
            switch_hospital.setChecked(false);
            switch_hospital.setClickable(false);
            mPrefs.edit().putBoolean(hospital, false).commit();


        } else if (v.getId() == R.id.save) {
            String string_quick_call = "quick_call";
            mPrefs.edit().putString(string_quick_call, quick_call).commit();

            String police = "police";
            mPrefs.edit().putBoolean(police, police_setting_status).commit();

            String hospital = "hospital";
            mPrefs.edit().putBoolean(hospital, hospital_setting_status).commit();
        }

    }
}