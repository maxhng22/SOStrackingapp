package com.example.sostrackingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class FakeCallRinging extends AppCompatActivity {

    private String networkCarrier;
    private MediaPlayer ringTone;
    TextView titleBar;
    TextView fakeNumber, fakename;
    ImageButton answerCall;
    ImageButton rejectCall;

    private SharedPreferences mPrefs;
    private MediaPlayer ringtone_mp, voice_mp;

    private String ringtone, voice_call, caller_name, caller_phone;
    //pref naming
    private final String pref_name = "caller_name";
    private final String pref_phone = "caller_phone";
    private final String pref_ringtone = "ringtone";
    private final String pref_voice = "voice";
    private final String pref_timer_label = "timer_label";

    static boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_call_ringing);
        active=true;

        getSharedPref();
        InitialiseControl();

        GetNetworkOperatorName();
        AssignFakeNumberAndDisplay();
        AssignFakeNameAndDisplay();

        if (ringtone.equals("default")) {
            StartRingTone();
        } else {
            StartCustomRingTone();
        }


        loadVoiceCall();

        answerCall.setOnClickListener(v -> {
            if (ringTone != null && ringTone.isPlaying()) {
                ringTone.stop();
            } else if (ringtone_mp != null && ringtone_mp.isPlaying()) {
                ringtone_mp.stop();
            }

            if (voice_mp != null) {
                voice_mp.start();
            }

        });

        rejectCall.setOnClickListener(v -> {

            if(ringTone!=null&&ringTone.isPlaying()){
                ringTone.stop();
            }

            if (ringtone_mp != null) {
                if (ringtone_mp.isPlaying()) {
                    ringtone_mp.stop();
                }
            }

//            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
//            homeIntent.addCategory(Intent.CATEGORY_HOME);
//            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(homeIntent);
            finish();
        });

    }

    private void getSharedPref() {
        mPrefs = getSharedPreferences("com.example.sostrackingapp", MODE_PRIVATE);
        caller_name = mPrefs.getString(pref_name, "Dad");
        caller_phone = mPrefs.getString(pref_phone, "013-4260001");
        ringtone = mPrefs.getString(pref_ringtone, "default");
        voice_call = mPrefs.getString(pref_voice, "default");
    }

    private void InitialiseControl() {
        fakeNumber = (TextView) findViewById(R.id.chosenfakenumber);
        fakename = (TextView) findViewById(R.id.chosenfakename);
        answerCall = (ImageButton) findViewById(R.id.answercall);
        rejectCall = (ImageButton) findViewById(R.id.rejectcall);
    }

    private void loadVoiceCall() {

        voice_mp = new MediaPlayer();
        Uri uri;

        uri = Uri.parse(getFilesDir().getPath() + File.separatorChar + "voice.mp3");


        File file = new File(getFilesDir().getPath() + File.separatorChar + "voice.mp3" );
        if (file.exists()) {
            Log.i("file here","exist");
        }else{
            String uriPath = "android.resource://" + getPackageName() + "/raw/default_voice";
            uri = Uri.parse(uriPath);
        }


        voice_mp = MediaPlayer.create(this, uri);
    }

    private void StartRingTone() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringTone = MediaPlayer.create(getApplicationContext(), notification);
        ringTone.start();
    }

    private void StartCustomRingTone() {
//        Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
        ringtone_mp = new MediaPlayer();
        try {
            ringtone_mp = MediaPlayer.create(this, Uri.parse(getFilesDir().getPath() + File.separatorChar + "ringtone.mp3"));
//            uri=Uri.parse(getFilesDir().getPath() + File.separatorChar + "ringtone.mp3");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ringtone_mp != null) {
            if (!ringtone_mp.isPlaying()) {
                ringtone_mp.start();
            }
        } else {
            StartRingTone();
        }
    }

    private void AssignFakeNumberAndDisplay() {
        fakeNumber.setText(caller_phone);
    }

    private void AssignFakeNameAndDisplay() {
        fakename.setText(caller_name);
    }

    private void GetNetworkOperatorName() {
        final TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        networkCarrier = tm.getNetworkOperatorName();

        titleBar = (TextView) findViewById(R.id.textView1);
        if (networkCarrier != null) {
            titleBar.setText("Incoming call - " + networkCarrier);
        } else {
            titleBar.setText("Incoming call");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        active=false;

        if (ringtone_mp != null) {
            if (ringtone_mp.isPlaying()) {
                ringtone_mp.stop();
            }
            ringtone_mp.release();
            ringtone_mp = null;
        }

        if (voice_mp != null) {
            if (voice_mp.isPlaying()) {
                voice_mp.stop();
            }
            voice_mp.release();
            voice_mp = null;
        }

        if (ringTone != null) {
            if (ringTone.isPlaying()) {
                ringTone.stop();
            }
            ringTone.release();
            ringTone = null;
        }

    }


}
