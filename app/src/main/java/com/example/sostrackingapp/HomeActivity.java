package com.example.sostrackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.sostrackingapp.MyService.LocalBinder;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    boolean mBounded;
    private Intent intent;
    private ValueEventListener a;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public static HomeActivity homeActivity;
    private double latitude;
    private double longitude;
    protected LocationManager locationManager;
    private final int REQUEST_LOCATION = 11;
    private int REQUEST_ID_MULTIPLE_PERMISSIONS = 1000;

    private SharedPreferences mPrefs;

    public void setLocation(double lat, double longit) {
        Log.i("here_location_update", "success");
        latitude = lat;
        longitude = longit;
    }
    private static ArrayList<Contact> data;
    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }


    MyService mServer;

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("BOOMBOOMTESTGPS", "not working");
            mBounded = false;
            mServer = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("BOOMBOOMTESTGPS", " working");
            mBounded = true;
            LocalBinder mLocalBinder = (LocalBinder) service;
            mServer = mLocalBinder.getServerInstance();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mPrefs = getSharedPreferences("com.example.sostrackingapp", MODE_PRIVATE);
//        Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();


        homeActivity = this;
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        findViewById(R.id.sos).setOnClickListener(this);
        findViewById(R.id.alarm).setOnClickListener(this);
        findViewById(R.id.call).setOnClickListener(this);
        findViewById(R.id.emergency).setOnClickListener(this);
        findViewById(R.id.battery).setOnClickListener(this);
        findViewById(R.id.tracking).setOnClickListener(this);


        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Write Function To enable gps
            OnGPS();
        }

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]
//                    {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CALL_PHONE}, REQUEST_LOCATION);
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                    HomeActivity.this,
//                    new String[]{Manifest.permission.CALL_PHONE},
//                    REQUEST_CODE);
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }else{
            Intent backgroundService = new Intent(getApplicationContext(), ScreenOnOffBackgroundService.class);
            startService(backgroundService);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), REQUEST_ID_MULTIPLE_PERMISSIONS);

        }

        data = new ArrayList<Contact>();
        a = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Contact key_Detail = snapshot.getValue(Contact.class);
                        data.add(key_Detail);
                    }

                    String json = gson.toJson(data);
                    mPrefs.edit().putString("contact_list", json).commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        ref.child(user.getUid()).child("Contact_Information").addValueEventListener(a);



        startService(new Intent(HomeActivity.this, MyService.class));
        Intent mIntent = new Intent(this, MyService.class);
        Log.i("BOOMBOOMTESTGPS", "BOOMBOOMTESTGPS");
        this.bindService(mIntent, mConnection, BIND_AUTO_CREATE);


        CheckSosMessafe();
    }

    public void CheckSosMessafe() {
        a = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    StringBuilder message = new StringBuilder();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        snapshot.getRef().child("Sos_Message").push().setValue(sosMessage);
                        SosMessage key_Detail = snapshot.getValue(SosMessage.class);

                        if (message.length() == 0) {
                            message.append(key_Detail.getPhone());
                        } else {
                            message.append(",");
                            message.append(key_Detail.getPhone());
                        }
                    }
                    message.append(" request SOS");
                    createDialog(message.toString(), dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        ref.child(user.getUid()).child("Sos_Message").orderByChild("status").equalTo("unseen").addListenerForSingleValueEvent(a);
    }

    private void createDialog(String message, DataSnapshot dataSnapshot) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        snapshot.getRef().child("Sos_Message").push().setValue(sosMessage);
                            snapshot.getRef().child("status").setValue("seen");
                        }

                    }


                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    }

    ;

    @Override
    protected void onStart() {
        super.onStart();

        Intent mIntent = new Intent(this, MyService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    }

    ;


    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.sos:
                i = new Intent(this, SosActivity.class);
                startActivity(i);
                break;
            case R.id.alarm:
                i = new Intent(this, AlarmActivity.class);
                startActivity(i);
                break;
            case R.id.call:
                i = new Intent(this, CallActivity.class);
                startActivity(i);
                break;
            case R.id.emergency:
                i = new Intent(this, EmergencyActivity.class);
                startActivity(i);
                break;
            case R.id.battery:
                i = new Intent(this, BatteryActivity.class);
                startActivity(i);
                break;
            case R.id.tracking:
                i = new Intent(this, GPSActivty.class);
                startActivity(i);
                break;

        }
    }


    private void OnGPS() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == 100) {
            if (!verifyAllPermissions(grantResults)) {
                Toast.makeText(getApplicationContext(), "No sufficient permissions", Toast.LENGTH_LONG).show();
            }else{

                Intent backgroundService = new Intent(getApplicationContext(), ScreenOnOffBackgroundService.class);
                startService(backgroundService);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean verifyAllPermissions(int[] grantResults) {

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}