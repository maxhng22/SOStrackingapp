package com.example.sostrackingapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class BatteryReceiver extends BroadcastReceiver {


    private static int percentage;
    private static String statusLabel;
    //    private static int percentage1;
    private final String pref_low_battery = "low_battery";
    private final String pref_sms = "sms";
    private boolean send_status = true;

    public static int getPercentage() {
        return percentage;
    }

    public static String getStatusLabel() {
        return statusLabel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_BATTERY_CHANGED)) {
            // Status
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

            switch (status) {
                case BatteryManager.BATTERY_STATUS_FULL:
                    statusLabel = "Full";
                    break;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    statusLabel = "Charging";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    statusLabel = "Discharging";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    statusLabel = "Not charging";
                    break;
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    statusLabel = "Unknown";
                    break;
                default:
                    statusLabel = "Nan";
            }

            // Percentage
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            percentage = level * 100 / scale;


            Bundle extras = intent.getExtras();
            Intent i = new Intent("Intent.ACTION_BATTERY_CHANGED");
            // Data you need to pass to activity
            i.putExtra("statusLabel", statusLabel);
            i.putExtra("percentage", percentage);
            context.sendBroadcast(i);

            if (percentage <= 35) {
                if(send_status) {
                    this.showNotification(context);
                    sendSms(context);
                }

//                send_status= false;
//
//                Handler handler3 = new Handler();
//                final Runnable r = new Runnable() {
//                    public void run() {
//                        send_status= true;
//                    }
//                };
//                handler3.postDelayed(r, 1800000);
            }
        }

    }


    private void showNotification(Context context) {

        SharedPreferences mPrefs = context.getSharedPreferences("com.example.sostrackingapp", MODE_PRIVATE);
        if (mPrefs.getBoolean(pref_low_battery, false)) {
//            sms_alert_status = mPrefs.getBoolean(pref_sms, false);
            Intent in = new Intent(context, BatteryMapActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,
                    0, in, 0);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "My Notification")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Sos Tracking Apps")
                    .setContentText("Device is running out of battery. Kindly recharge.")
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Channel Name";// The user-visible name of the channel.
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel("My Notification", name, importance);
                notificationManager.createNotificationChannel(mChannel);
            }
            notificationManager.notify(0, notificationBuilder.build()); // 0 is the request code, it should be unique id

        }
    }

    private void sendSms(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences("com.example.sostrackingapp", MODE_PRIVATE);
//
        if (mPrefs.getBoolean(pref_sms, false)) {
//
            SingleShotLocationProvider.requestSingleUpdate(context,
                    location -> {
                        Log.d("Location", "my location is " + location.toString());
//                    local_intent.putExtra("type", type);

                        String message = "SOS Alert! My battery is running out" +
                                "My location is :"+
                                "https://www.google.com/maps/search/?api=1&query="+location.latitude + "," + location.longitude;

                        Log.i("location", message);

                        Gson gson = new Gson();
                        String json = mPrefs.getString("contact_list", "");

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

}




