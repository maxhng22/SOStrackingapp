package com.example.sostrackingapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContactTrackingActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String name,phone;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ValueEventListener a;
    float zoomLevel = 16.0f;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_tracking);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        name = intent.getStringExtra("name");

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
//mMap.getUiSettings().zoo
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        getLocation();

    }

    public void getLocation(){
//        Toast.makeText(ContactTrackingActivity.this, "exist", Toast.LENGTH_LONG).show();
        a = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        try{
                            User key_Detail = snapshot.child("User_Information").getValue(User.class);
                            if(key_Detail!=null){
                                Log.i("here",key_Detail.toString());
                                setMarker(key_Detail.getLongitude(), key_Detail.getLatitude());
                            }else{
                                Toast.makeText(ContactTrackingActivity.this, "Not Found", Toast.LENGTH_LONG).show();
                            }
                        }catch(Exception e){
                            Toast.makeText(ContactTrackingActivity.this, "Not Found", Toast.LENGTH_LONG).show();
                            setMarker(0, 0);
                            e.printStackTrace();
                        }


//                        if(key_Detail!=null){
//                            setMarker(key_Detail.getLongitude(), key_Detail.getLatitude());
//                        }else{
//                            Toast.makeText(ContactTrackingActivity.this, "Not Found", Toast.LENGTH_LONG).show();
//                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        ref.orderByChild("User_Information/phone").equalTo(phone).addValueEventListener(a);
    }

    public void setMarker(double longtitude, double latitude){
      Log.i("long",String.valueOf(longtitude));
        Log.i("lat",String.valueOf(latitude));
        if(longtitude==0&&latitude==0){
            Toast.makeText(ContactTrackingActivity.this, "Not Found", Toast.LENGTH_LONG).show();
        }else{
            if(marker!=null){
                Log.i("here", "not null");
                LatLng location = new LatLng(latitude, longtitude);
                marker.setPosition(location);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
            }else{
                Log.i("here", "not null111");
                LatLng location = new LatLng(latitude, longtitude);
//                marker=new MarkerOptions().position(location).title(name);
                marker= mMap.addMarker(new MarkerOptions().position(location).title(name));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
            }
        }

//        mMap.moveCamera(CameraUpdateFactory.newLatLng(location,zoomLevel));
    }
}

