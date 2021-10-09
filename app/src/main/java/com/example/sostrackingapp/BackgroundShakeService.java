package com.example.sostrackingapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class BackgroundShakeService extends Service implements shake.Listener {
    //    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    private int volume;
    private String  pref_sos_sms="sos_sms";
    private String  pref_sos_message="sos_message";
    private MediaPlayer mp;
    private final IBinder musicBind = new LocalBinder();
    SensorManager sensorManager;
    shake sd;
    private boolean runned=true;

    @Override
    public void onCreate() {

//        mp = new MediaPlayer();
//        mp = MediaPlayer.create(getApplicationContext(), R.raw.police);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sd = new shake(this);
        sd.start(sensorManager);
    }

    @Override
    public void hearShake() {

        if(runned){
            runned=false;
            if(mp!=null){
                if (mp.isPlaying()) {
                    Toast.makeText(getApplicationContext(),"stop",Toast.LENGTH_LONG).show();
                    mp.pause();
                    Log.d("Audio status:", "Stop...");
                } else {
                    Toast.makeText(getApplicationContext(),"started",Toast.LENGTH_LONG).show();
                    mp.start();
                    Log.d("Audio status:", "Playing...");
                }
            }

            final Handler handler = new Handler();
            final Runnable r = () -> runned=!runned;
            handler.postDelayed(r, 1000);
        }
        sendSms();
    }

    private void sendSms() {
        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("com.example.sostrackingapp", MODE_PRIVATE);
//
        if (mPrefs.getBoolean(pref_sos_sms, false)) {
//
            SingleShotLocationProvider.requestSingleUpdate(getApplicationContext(),
                    location -> {
                        Log.d("Location", "my location is " + location.toString());
//                    local_intent.putExtra("type", type);

//                        String message = "Alert! Safety SOS Tracking App. " +
//                                "My phone is running out of battery! " +
//                                "This is my current location :" +
//                                "https://www.google.com/maps/search/?api=1&query=" + location.latitude + "," + location.longitude;

                        Gson gson = new Gson();
                        String json = mPrefs.getString("contact_list", "");
                        String message = mPrefs.getString(pref_sos_message, "Hi! I need Help!!")+"My location at: https://www.google.com/maps/search/?api=1&query=" + location.latitude + "," + location.longitude;

                        if (!json.equals("")) {
                            try {
                                Type type = new TypeToken<List<Contact>>() {
                                }.getType();
                                List<Contact> arrayList = gson.fromJson(json, type);
                                for (Contact contact : arrayList) {

                                    SmsManager smgr = SmsManager.getDefault();
                                    Log.i("phone num", contact.getPhone());
                                    smgr.sendTextMessage(contact.getPhone(), null, message, null, null);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    });


        }
    }

    public void setMusicFile(Uri file, float volume, boolean loop) {

        mp = new MediaPlayer();
        mp = MediaPlayer.create(getApplicationContext(), file);

        if(mp!=null){
            mp.setVolume(volume, volume); //set volume takes two paramater
            if (loop) {
                mp.setLooping(true);
            } else {
                mp.setLooping(false);
            }
        }else{
            mp = MediaPlayer.create(getApplicationContext(), R.raw.police);
        }

    }

    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return musicBind;
    }

    public class LocalBinder extends Binder {
        public BackgroundShakeService getServerInstance() {
            return BackgroundShakeService.this;
        }
    }

    public BackgroundShakeService() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
            mp = null;
        }

        sd.stop();
        stopForeground(true);
    }

    public void executeStopForeground()
    {
        stopForeground(true);
    }

//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
}