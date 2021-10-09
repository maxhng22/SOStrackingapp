package com.example.sostrackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BatterySettingActivity extends AppCompatActivity  implements View.OnClickListener {


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ValueEventListener a, b;


    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<Contact> data;

    private SwitchCompat sms_alert;
    private SwitchCompat low_battery;
    private Button add_contact;


    private SharedPreferences mPrefs;
    private boolean low_battery_status = false;
    private boolean sms_alert_status = false;


    private final String pref_low_battery = "low_battery";
    private final String pref_sms = "sms";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_setting);


//        smsReceiver=new SmsReceiver();
//        IntentFilter mIntentFilter = new IntentFilter();
//        mIntentFilter.addAction("android.intent.action.BATTERY_CHANGED");
//        registerReceiver(smsReceiver, mIntentFilter);

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        mPrefs = getSharedPreferences("com.example.sostrackingapp", MODE_PRIVATE);
        low_battery_status = mPrefs.getBoolean(pref_low_battery, false);
        sms_alert_status = mPrefs.getBoolean(pref_sms, false);


//        add_contact = (Button) findViewById(R.id.add_contact);
        low_battery = (SwitchCompat) findViewById(R.id.low_battery);
        sms_alert = (SwitchCompat) findViewById(R.id.sms);
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.add_contact).setOnClickListener(this);
        findViewById(R.id.reset).setOnClickListener(this);


        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<Contact>();
        adapter = new ContactProfileAdapter(data, 3, this);
        recyclerView.setAdapter(adapter);

        low_battery.setChecked(low_battery_status);
        sms_alert.setChecked(sms_alert_status);

        low_battery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                low_battery_status = isChecked;
            }
        });

        //switch on checked listener
        sms_alert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sms_alert_status = isChecked;
            }
        });

        //populate the adapter
        a = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                data.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Contact key_Detail = snapshot.getValue(Contact.class);
                        data.add(key_Detail);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        ref.child(user.getUid()).child("Contact_Information").addValueEventListener(a);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.reset) {
            low_battery.setChecked(false);
            mPrefs.edit().putBoolean(pref_low_battery, false).commit();

            sms_alert.setChecked(false);
            mPrefs.edit().putBoolean(pref_sms, false).commit();

        } else if (v.getId() == R.id.save) {

            mPrefs.edit().putBoolean(pref_low_battery, low_battery_status).commit();
            mPrefs.edit().putBoolean(pref_sms, sms_alert_status).commit();

        }else if(v.getId() == R.id.add_contact){
            Intent intent = new Intent(BatterySettingActivity.this, AddContactActivity.class);
            startActivity(intent);
        }

    }
}