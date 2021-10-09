package com.example.sostrackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.sostrackingapp.LoginActivity;
import com.example.sostrackingapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextpassword, editTextemail, editTextconfirmpassword;
    private EditText editTextfirstname,editTextphone;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ValueEventListener a;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

Log.i("ref is here", ref.toString());
        editTextpassword = findViewById(R.id.password_edit_text);
        editTextconfirmpassword = findViewById(R.id.confirm_password_edit_text);
        editTextemail = findViewById(R.id.email_edit_text);
        editTextfirstname = findViewById(R.id.first_name_edit_text);
        editTextphone = findViewById(R.id.phone_edit_text);
        auth = FirebaseAuth.getInstance();



        findViewById(R.id.email_sign_up_button).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.email_sign_up_button:
                final String email = editTextemail.getText().toString().trim();
                final String password = editTextpassword.getText().toString().trim();
                final String confirmpassword = editTextconfirmpassword.getText().toString().trim();
                final String username = editTextfirstname.getText().toString().trim();

                final String phone = editTextphone.getText().toString().trim();


                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), "Username and phone number cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Email address cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmpassword)) {
                    Toast.makeText(getApplicationContext(), "Password and confirm password is not same!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //create used
                checkPhone(value -> {
                    if (!value) {
                        auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        if (!task.isSuccessful()) {
                                            // there was an error
                                            Toast.makeText(RegisterActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                        } else {
                                            auth = FirebaseAuth.getInstance();
                                            user = auth.getCurrentUser();
                                            User updated_user = new User();
                                            updated_user.setUsername(username);
                                            updated_user.setEmail(email);
                                            updated_user.setPhone(phone);

                                            ref.child(user.getUid()).child("User_Information").setValue(updated_user, new DatabaseReference.CompletionListener() {
                                                public void onComplete(DatabaseError error, @NonNull DatabaseReference ref) {

                                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });

                                        }
                                    }
                                });

                    }else{
                        Toast.makeText(getApplicationContext(), "duplicate number!", Toast.LENGTH_SHORT).show();
                    }
                }, phone);
                break;
//            case R.id.sign_in:
////                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
////                startActivity(intent);
//                finish();
//                break;
        }
    }

    public interface MyCallback {
        void onCallback(Boolean value);
    }
    public void checkPhone(AddContactActivity.MyCallback myCallback, String phone) {

        a = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    myCallback.onCallback(true);
                } else {
                    myCallback.onCallback(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };


        ref.orderByChild("User_Information/phone").equalTo(phone).addListenerForSingleValueEvent(a);


    }
}

