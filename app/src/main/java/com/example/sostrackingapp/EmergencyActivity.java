package com.example.sostrackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.Manifest;
import android.widget.Toast;

public class EmergencyActivity extends AppCompatActivity implements View.OnClickListener {

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    protected LocationManager locationManager;
    private Location Location_cur;
    private Intent intent;
    private int REQUEST_CODE = 1000;
    public final static int REQUEST_CODE_POWER_BUTTON = 10101;
    private final int REQUEST_LOCATION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        findViewById(R.id.call_hospital).setOnClickListener(this);
        findViewById(R.id.call_police).setOnClickListener(this);
        findViewById(R.id.locate_nearby_hospital).setOnClickListener(this);
        findViewById(R.id.locate_nearby_police).setOnClickListener(this);
        findViewById(R.id.setting).setOnClickListener(this);

//        if (checkDrawOverlayPermission()) {
//            startService(new Intent(this, PowerButtonService.class));
//        }

    }
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
//            Toast.makeText(EmergencyActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
//            Log.i("here","rqweqwe");
//            return true;
//        }
//
//        return super.dispatchKeyEvent(event);
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Toast.makeText(EmergencyActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
//        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
//            Toast.makeText(EmergencyActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }


//    @Override
//    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_POWER) {
//            Toast.makeText(EmergencyActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
//          Log.i("here","rqweqwe");
//            return true;
//        }
//        return super.onKeyLongPress(keyCode, event);
//    }

//    public boolean checkDrawOverlayPermission() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (!Settings.canDrawOverlays(this)) {
//            /** if not construct intent to request permission */
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + getPackageName()));
//            /** request permission via start activity for result */
//            startActivityForResult(intent, REQUEST_CODE_POWER_BUTTON);
//            return false;
//        } else {
//            return true;
//        }
//    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_POWER_BUTTON) {
            if (Settings.canDrawOverlays(this)) {
                startService(new Intent(this, ScreenOnOffActivity.class));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.call_hospital) {

            if (ActivityCompat.checkSelfPermission(EmergencyActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Uri callUri = Uri.parse("tel:01110082709 ");
                Log.i("here ", "here");
                Intent callIntent = new Intent(Intent.ACTION_CALL, callUri);
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                startActivity(callIntent);
            } else {
                ActivityCompat.requestPermissions(
                        EmergencyActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        REQUEST_CODE);
            }

        } else if (v.getId() == R.id.call_police) {
            if (ActivityCompat.checkSelfPermission(EmergencyActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Uri callUri = Uri.parse("tel:01110082709");
                Intent callIntent = new Intent(Intent.ACTION_CALL, callUri);
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                startActivity(callIntent);
            } else {
                ActivityCompat.requestPermissions(
                        EmergencyActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        REQUEST_CODE);
            }
        } else if (v.getId() == R.id.locate_nearby_hospital) {
            intent = new Intent(EmergencyActivity.this, EmergencyMapActivity.class);
            checkLocationStatus(intent, "hospital");
        } else if (v.getId() == R.id.locate_nearby_police) {
            intent = new Intent(EmergencyActivity.this, EmergencyMapActivity.class);
            checkLocationStatus(intent, "police");
        } else if (v.getId() == R.id.setting) {
            intent = new Intent(EmergencyActivity.this, EmergencySettingActivity.class);
            startActivity(intent);
        }

    }

    public void checkLocationStatus(Intent local_intent, String type) {
        Log.i("is here", "getting in fucntion");

        locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Write Function To enable gps
            Log.i("is here", "getting gps");
            OnGPS();
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("is here", "getting permisson");
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location_cur = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (Location_cur == null) {
                Location_cur = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (Location_cur == null) {
                Location_cur = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }

            if (Location_cur == null) {
                Log.d("Location", "my location is donno");
                SingleShotLocationProvider.requestSingleUpdate(this,
                        location -> {
                            Log.d("Location", "my location is " + location.toString());
                            local_intent.putExtra("type", type);
                            local_intent.putExtra("longitude", location.longitude);
                            local_intent.putExtra("latitude", location.latitude);
                            startActivity(local_intent);
//                            Toast.makeText(getApplicationContext(), "Permissions granted88"+location.toString(), Toast.LENGTH_LONG).show();
                            Log.d("Location", "my location is " + location.toString());
                        });
            } else {
//                Toast.makeText(getApplicationContext(), "Permissions granted22"+Location_cur.toString(), Toast.LENGTH_LONG).show();
                Log.i("not here", Location_cur.toString());
                local_intent.putExtra("type", type);
                local_intent.putExtra("longitude", Location_cur.getLongitude());
                local_intent.putExtra("latitude", Location_cur.getLatitude());
                startActivity(local_intent);
            }

//            Log.i("longtitude","lol"+Location_cur.getLongitude());
//            Log.i("longtitude","lol"+Location_cur.getLongitude());
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == 100) {
            if (!verifyAllPermissions(grantResults)) {
                Toast.makeText(getApplicationContext(), "No sufficient permissions", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Permissions granted", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "No sufficient permissions", Toast.LENGTH_SHORT).show();
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