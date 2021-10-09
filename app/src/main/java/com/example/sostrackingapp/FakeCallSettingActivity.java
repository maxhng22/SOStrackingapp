package com.example.sostrackingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class FakeCallSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText name_edit_text, phone_edit_text;
    private RadioGroup radioGroup1, radioGroup2;
    private String ringtone, voice_call, caller_name, caller_phone,timer_label;
    private boolean timer;
    private SharedPreferences mPrefs;
    private int timer_duration;
    private final int REQ_CODE_PICK_SOUNDFILE = 10;

    private String current_Op, file_name;
    private Button timer_button;


    //pref naming
    private final String pref_name = "caller_name";
    private final String pref_phone = "caller_phone";
    private final String pref_timer = "timer";
    private final String pref_ringtone = "ringtone";
    private final String pref_duration = "timer_duration";
    private final String pref_voice = "voice";
    private final String pref_timer_label = "timer_label";

    private String[] arraySpinner_duration = new String[]{
            "05 seconds", "10 seconds", "30 seconds", "60 seconds", "02 minutes", "10 minutes", "20 minutes", "30 minutes", "60 minutes"
    };

    private final Map<String, Integer> duration_milli = new HashMap<String, Integer>() {{
        put("05 seconds", 5000);
        put("10 seconds", 10000);
        put("30 seconds", 30000);
        put("60 seconds", 60000);
        put("02 minutes", 120000);
        put("10 minutes", 600000);
        put("20 minutes", 1200000);
        put("30 minutes", 1800000);
        put("60 minutes", 3600000);
    }};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        mPrefs = getSharedPreferences("com.example.sostrackingapp", MODE_PRIVATE);
        caller_name = mPrefs.getString(pref_name, "Dad");
        caller_phone = mPrefs.getString(pref_phone, "013-4260001");
        timer = mPrefs.getBoolean(pref_timer, false);
        ringtone = mPrefs.getString(pref_ringtone, "default");
        timer_duration = mPrefs.getInt(pref_duration, 30000);
        voice_call = mPrefs.getString(pref_voice, "default");
        timer_label= mPrefs.getString(pref_timer_label, "30 seconds");

        radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
        radioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
        name_edit_text = (EditText) findViewById(R.id.name_edit_text);
        phone_edit_text = (EditText) findViewById(R.id.phone_edit_text);
        timer_button = (Button) findViewById(R.id.timer);

        //button
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.reset).setOnClickListener(this);
        findViewById(R.id.timer).setOnClickListener(this);
        findViewById(R.id.ringtone_file).setOnClickListener(this);
        findViewById(R.id.voice_file).setOnClickListener(this);


        radioGroup1.setOnCheckedChangeListener((group, checkedId) -> {
            //set timer status
            if (checkedId == R.id.radio0) {
                timer = true;
            } else if (checkedId == R.id.radio1) {
                timer = false;
            }
        });

        radioGroup2.setOnCheckedChangeListener((group, checkedId) -> {
            //set ringtone
            if (checkedId == R.id.radio2) {
                ringtone = "default";
            } else if (checkedId == R.id.radio3) {
                ringtone = "custom";
            }
        });


        name_edit_text.setText(caller_name);
        phone_edit_text.setText(caller_phone);
        timer_button.setText(timer_label);

        RadioButton radioButton;
        if (timer) {
            radioButton = (RadioButton) findViewById(R.id.radio0);
        } else {
            radioButton = (RadioButton) findViewById(R.id.radio1);
        }
        radioButton.setChecked(true);


        RadioButton radioButton2;
        if (ringtone.equals("default")) {
            radioButton2 = (RadioButton) findViewById(R.id.radio2);
        } else {
            radioButton2 = (RadioButton) findViewById(R.id.radio3);
        }
        radioButton2.setChecked(true);


    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.ringtone_file) {
            current_Op = "ringtone_url";
            file_name = "ringtone.mp3";
            Intent audiofile_chooser_intent;
            audiofile_chooser_intent = new Intent();
            audiofile_chooser_intent.setAction(Intent.ACTION_GET_CONTENT);
            audiofile_chooser_intent.setType("audio/*");
            startActivityForResult(Intent.createChooser(audiofile_chooser_intent, getString(R.string.select_audio_file_title)), REQ_CODE_PICK_SOUNDFILE);
        } else if (v.getId() == R.id.voice_file) {
            current_Op = "voice_url";
            file_name = "voice.mp3";
            Intent audiofile_chooser_intent;
            audiofile_chooser_intent = new Intent();
            audiofile_chooser_intent.setAction(Intent.ACTION_GET_CONTENT);
            audiofile_chooser_intent.setType("audio/*");
            startActivityForResult(Intent.createChooser(audiofile_chooser_intent, getString(R.string.select_audio_file_title)), REQ_CODE_PICK_SOUNDFILE);

        } else if (v.getId() == R.id.timer) {


            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    R.layout.simple_spinner_item1, R.id.textView, arraySpinner_duration);
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
//            timer_button.setAdapter(adapter);

            new AlertDialog.Builder(this)
                    .setTitle("Select duration")
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // TODO: user specific action
//                            Toast.makeText(FakeCallSettingActivity.this, arraySpinner_duration[which], Toast.LENGTH_LONG).show();
                            timer_button.setText(arraySpinner_duration[which]);
                            timer_label=arraySpinner_duration[which];
                            if (duration_milli.get(arraySpinner_duration[which]) != null) {
                                timer_duration = duration_milli.get(arraySpinner_duration[which]);
                            }
                            Log.i("string check",String.valueOf(timer_duration));
                            dialog.dismiss();
                        }
                    }).create().show();

        } else if (v.getId() == R.id.save) {

            caller_name = name_edit_text.getText().toString();
            caller_phone = phone_edit_text.getText().toString();

            mPrefs.edit().putString(pref_name, caller_name).commit();

            mPrefs.edit().putString(pref_phone, caller_phone).commit();

            mPrefs.edit().putBoolean(pref_timer, timer).commit();

            mPrefs.edit().putString(pref_ringtone, ringtone).commit();

            mPrefs.edit().putInt(pref_duration, timer_duration).commit();

            mPrefs.edit().putString(pref_timer_label, timer_label).commit();

        } else if (v.getId() == R.id.reset) {

            name_edit_text.setText(R.string.default_caller_name);
            phone_edit_text.setText(R.string.default_caller_phone);
            timer_button.setText(R.string.set_timer);

            RadioButton radioButton = (RadioButton) findViewById(R.id.radio1);
            radioButton.setChecked(true);

            RadioButton radioButton2 = (RadioButton) findViewById(R.id.radio2);
            radioButton2.setChecked(true);

            caller_name = "Dad";
            caller_phone = "013-4260001";
            timer = false;
            ringtone = "default";
            timer_duration = 20;
            voice_call = "default";

            mPrefs.edit().putString(pref_name, "Dad").commit();

            mPrefs.edit().putString(pref_phone, "013-4260001").commit();

            mPrefs.edit().putBoolean(pref_timer, false).commit();

            mPrefs.edit().putString(pref_ringtone, "default").commit();

            mPrefs.edit().putInt(pref_duration, 30000).commit();

            mPrefs.edit().putString(pref_timer_label, "30 seconds").commit();

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_PICK_SOUNDFILE && resultCode == Activity.RESULT_OK) {
            if ((data != null) && (data.getData() != null)) {
                Uri audioFileUri = data.getData();
                Log.i("url is here", audioFileUri.toString());
                try {
                    savefile(audioFileUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mPrefs.edit().putString("audio_url", audioFileUri.toString()).apply();
                // Now you can use that Uri to get the file path, or upload it, ...
            }
        }
    }

    void savefile(Uri sourceuri) {
        String sourceFilename = sourceuri.getPath();
        String destinationFilename = getFilesDir().getPath() + File.separatorChar + file_name;

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
//            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bis = new BufferedInputStream(getContentResolver().openInputStream(sourceuri));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
            mPrefs.edit().putString(current_Op, destinationFilename).apply();
            Log.i("url is here", destinationFilename);
            Log.i("url is here", new File(destinationFilename).exists() + "lol");
            Log.i("url is here", new File(destinationFilename).length() + "p");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}