package com.example.sostrackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SosSetting extends AppCompatActivity implements View.OnClickListener {


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ValueEventListener a;
    private String  pref_sos_message="sos_message";
    private EditText message;
    private SharedPreferences mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_setting);

        mPrefs = getSharedPreferences("com.example.sostrackingapp", MODE_PRIVATE);
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        message=findViewById(R.id.message);

        a = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String exist_messafe=dataSnapshot.child("message").getValue(String.class);
                    if(!exist_messafe.equals("")){
                        message.setText(exist_messafe);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        ref.child(user.getUid()).child("Sos_Setting").addListenerForSingleValueEvent(a);

        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.add:
                String message_string = message.getText().toString().trim();
                //validation
                if (TextUtils.isEmpty(message_string)) {
                    Toast.makeText(getApplicationContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                mPrefs.edit().putString(pref_sos_message, message_string).commit();
                ref.child(user.getUid()).child("Sos_Setting").child("message").setValue(message_string, new DatabaseReference.CompletionListener() {
                    public void onComplete(DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(getApplicationContext(), "Update message successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });


                break;
            case R.id.back:

                finish();
                break;
        }
    }
}