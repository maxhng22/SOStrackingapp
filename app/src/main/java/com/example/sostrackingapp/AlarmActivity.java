package com.example.sostrackingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skyfishjy.library.RippleBackground;

import java.io.File;
import java.io.IOException;


public class AlarmActivity extends AppCompatActivity {

    private boolean scream_status = false;
    private boolean shake_status = false;
    boolean mBounded=false;
    private MediaPlayer mp;
    private SharedPreferences mPrefs;
    private float log1;
    private Uri uri;
    private boolean loop;
    private Intent mIntent;
    private Intent intent;
    private FloatingActionButton setting;
    private SwitchCompat shake_pref;
    private RippleBackground rippleBackground;
    private Button scream_button;

    BackgroundShakeService mServer;

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("Shake service", "not working");
            mBounded = false;
            mServer = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("Shake service", " working");
            mBounded = true;
            BackgroundShakeService.LocalBinder mLocalBinder = (BackgroundShakeService.LocalBinder) service;
            mServer = mLocalBinder.getServerInstance();
//            if (mp != null) {
//                mServer.setMp(mp);
//            } else {
                mServer.setMusicFile(uri, log1, loop);
//            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        //shared pref
        mIntent = new Intent(AlarmActivity.this, BackgroundShakeService.class);

        //switch
//        shake_pref = (SwitchCompat) findViewById(R.id.mySwitch);
//        shake_pref.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                serviceStart(isChecked);
//                if (isChecked) {
//                    shake_status = true;
//                } else {
//                    shake_status = false;
//                }
//                mPrefs.edit().putBoolean(shake, shake_status).apply();
//            }
//        });

        //scream button and ripple animation effect
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        scream_button = (Button) findViewById(R.id.centerImage);
        scream_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setScreamButton(scream_status);
            }
        });
//
        setting = findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(AlarmActivity.this, AlarmSettingActivity.class);
                startActivity(intent);
            }
        });

        setMediaPlayer();



    }

    private void serviceStart(boolean status) {
        if (status) {
//            Intent mIntent = new Intent(AlarmActivity.this, BackgroundShakeService.class);
            if (!mBounded) {
                Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_SHORT).show();
                startService(mIntent);
                AlarmActivity.this.bindService(mIntent, mConnection, BIND_AUTO_CREATE);
            }else{
                Toast.makeText(getApplicationContext(), "Service started not bounded", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Service started false", Toast.LENGTH_SHORT).show();
            if (mBounded) {
                unbindService(mConnection);
                stopService(mIntent);
                mBounded = false;
            }
        }
    }

    private void setMediaPlayer() {
//        Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_SHORT).show();
        mPrefs = getSharedPreferences("com.example.sostrackingapp", MODE_PRIVATE);

        if (mp != null) {
            try {
                if (mp.isPlaying()) {
                    mp.stop();
                }
                mp.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        int currVolume = mPrefs.getInt("volume", 99);
        int maxVolume = 100;
        log1 = (float) (1 - (Math.log(maxVolume - currVolume) / Math.log(maxVolume)));

        //get selected file
        String audio_file = mPrefs.getString("audio_file", "female");
        String uriPath = "android.resource://" + getPackageName() + "/raw/" + audio_file;
        uri = Uri.parse(uriPath);

        //get loop status
        loop = mPrefs.getBoolean("loop", true);

        //set media player
        mp = new MediaPlayer();
        if (audio_file.equals("custom")) {
            try {
                mp = MediaPlayer.create(this, Uri.parse(getFilesDir().getPath() + File.separatorChar + "abc.mp3"));
                uri=Uri.parse(getFilesDir().getPath() + File.separatorChar + "abc.mp3");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mp = MediaPlayer.create(this, uri);
        }

        if (mp != null) {
            mp.setVolume(log1, log1); //set volume takes two paramater
            mp.setLooping(loop);
        }

        serviceStart(mPrefs.getBoolean("shake", false));

    }

    @Override
    protected void onResume() {
        super.onResume();
        setMediaPlayer();
    }


    private void setScreamButton(boolean status) {
        if (status) {
            rippleBackground.stopRippleAnimation();
            scream_status = false;
//                  mp
            final Handler handler = new Handler();
            final Runnable r = () -> scream_button.setText(R.string.scream);
            handler.postDelayed(r, 150);

            //stop screaming audio
            if (mp!=null&&mp.isPlaying()) {
                mp.pause();
            }

        } else {
            scream_status = true;
            rippleBackground.startRippleAnimation();
//                    prefs.edit().putBoolean(shake, scream_status).apply();
            final Handler handler = new Handler();
            final Runnable r = () -> scream_button.setText(R.string.stop_scream);
            handler.postDelayed(r, 500);

            //play screaming sound
            if(mp!=null)
            if (mp.isPlaying()) {
                mp.pause();
            } else {
                mp.start();
            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
            mp = null;
        }
        setScreamButton(true);
    }

    ;
}