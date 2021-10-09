package com.example.sostrackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.loader.content.CursorLoader;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AlarmSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private String audio_file;
    private final int REQ_CODE_PICK_SOUNDFILE = 10;
    private boolean loop = false;
    private SharedPreferences mPrefs;
    private String  pref_sos_sms="sos_sms";

    private SeekBar seek;
    private int volume = 20;
    private SwitchCompat switchloop;
    private boolean shake_status;
    RadioGroup rGroup;
    private SwitchCompat shake_pref;
    private SwitchCompat shake_sms_pref;
    boolean shake_pref_sms;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);

//        SharedPreferences prefs = getSharedPreferences("packagename", Context.MODE_PRIVATE);
        //get shared preference
        mPrefs = getSharedPreferences("com.example.sostrackingapp", MODE_PRIVATE);
        int currVolume_pref = mPrefs.getInt("volume", 20);
        String audio_file_pref = mPrefs.getString("audio_file", "female");
        boolean loop_pref = mPrefs.getBoolean("loop", true);
        boolean shake_pref_status = mPrefs.getBoolean("shake", true);
        shake_pref_sms = mPrefs.getBoolean(pref_sos_sms, true);
        volume=currVolume_pref;
        audio_file=audio_file_pref;
        loop=loop_pref;
        shake_status=shake_pref_status;

        //set id
        rGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        Button file = (Button) findViewById(R.id.file);
        switchloop = (SwitchCompat) findViewById(R.id.mySwitch);
        seek = (SeekBar) findViewById(R.id.seekBar_luminosite);
        shake_pref = (SwitchCompat) findViewById(R.id.shake);
        shake_sms_pref = (SwitchCompat) findViewById(R.id.shake_sms);


        //set onClickListener
        file.setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.reset).setOnClickListener(this);


        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);


        switchloop.setChecked(loop_pref);
        seek.setProgress(currVolume_pref);
        shake_pref.setChecked(shake_pref_status);
        shake_sms_pref.setChecked(shake_pref_sms);
        switch (audio_file_pref) {
            case "male":
                RadioButton radioButton = (RadioButton) findViewById(R.id.radio0);
                radioButton.setChecked(true);
                break;
            case "female":
                RadioButton radioButton1 = (RadioButton) findViewById(R.id.radio1);
                radioButton1.setChecked(true);
                break;
            case "police":
                RadioButton radioButton2 = (RadioButton) findViewById(R.id.radio2);
                radioButton2.setChecked(true);
                break;
            default:
                RadioButton radioButton3 = (RadioButton) findViewById(R.id.radio3);
                radioButton3.setChecked(true);
                file.setEnabled(true);
                break;
        }


        rGroup.setOnCheckedChangeListener((group, checkedId) -> {

            //set audio_file
            if (checkedId == R.id.radio3) {
                audio_file = "custom";
                file.setEnabled(true);
            } else if (checkedId == R.id.radio0) {
                audio_file = "male";
                file.setEnabled(false);
            } else if (checkedId == R.id.radio1) {
                audio_file = "female";
                file.setEnabled(false);
            } else if (checkedId == R.id.radio2) {
                audio_file = "police";
                file.setEnabled(false);
            }

        });

        switchloop.setOnCheckedChangeListener((buttonView, isChecked) -> loop = isChecked);
        shake_pref.setOnCheckedChangeListener((buttonView, isChecked) -> shake_status=isChecked);
        shake_sms_pref.setOnCheckedChangeListener((buttonView, isChecked) -> shake_pref_sms=isChecked);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
//                Toast.makeText(getApplicationContext(), "Volume:"+String.valueOf(progress), Toast.LENGTH_SHORT).show();
                volume = progress;
            }
        });

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.file) {
//            Intent videoIntent = new Intent(
//                    Intent.ACTION_PICK,
//                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(Intent.createChooser(videoIntent, "Select Audio"), REQ_CODE_PICK_SOUNDFILE);

//            Intent audioIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(audioIntent,REQ_CODE_PICK_SOUNDFILE);

            Intent audiofile_chooser_intent;
            audiofile_chooser_intent = new Intent();
            audiofile_chooser_intent.setAction(Intent.ACTION_GET_CONTENT);
            audiofile_chooser_intent.setType("audio/*");
            startActivityForResult(Intent.createChooser(audiofile_chooser_intent, getString(R.string.select_audio_file_title)), REQ_CODE_PICK_SOUNDFILE);
        } else if (v.getId() == R.id.save) {
            String looping = "loop";

            Log.d("test", "testing c " + mPrefs.getBoolean(looping, false));
            mPrefs.edit().putBoolean(looping, loop).commit();
            Log.d("test", "testing d " + mPrefs.getBoolean(looping, false));

            String string_audio_file = "audio_file";
            mPrefs.edit().putString(string_audio_file, audio_file).commit();
            String sound_volume = "volume";
            mPrefs.edit().putInt(sound_volume, volume).commit();

            mPrefs.edit().putBoolean("shake", shake_status).commit();

            mPrefs.edit().putBoolean(pref_sos_sms, shake_pref_sms).commit();
        } else if (v.getId() == R.id.reset) {

            String looping = "loop";
            loop = true;
            switchloop.setChecked(true);
            mPrefs.edit().putBoolean(looping, loop).commit();

            String string_audio_file = "audio_file";
            audio_file = "female";
            RadioButton radioButton = (RadioButton) findViewById(R.id.radio1);
            radioButton.setChecked(true);
            mPrefs.edit().putString(string_audio_file, audio_file).commit();

            String sound_volume = "volume";
            volume = 20;
            seek.setProgress(volume);
            mPrefs.edit().putInt(sound_volume, volume).commit();

            String shake = "shake";
            shake_pref.setChecked(false);
            mPrefs.edit().putBoolean(shake, false).commit();

            shake_sms_pref.setChecked(false);
            mPrefs.edit().putBoolean(pref_sos_sms, false).commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_PICK_SOUNDFILE && resultCode == Activity.RESULT_OK) {
            if ((data != null) && (data.getData() != null)) {
                Uri audioFileUri = data.getData();
                Log.i("url is here",audioFileUri.toString());
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

    void savefile(Uri sourceuri)
    {
        String sourceFilename= sourceuri.getPath();
        String destinationFilename = getFilesDir().getPath()+ File.separatorChar+"abc.mp3";

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
            } while(bis.read(buf) != -1);
            mPrefs.edit().putString("audio_url", destinationFilename).apply();
            Log.i("url is here",destinationFilename);
            Log.i("url is here",new File(destinationFilename).exists()+"lol");
            Log.i("url is here",new File(destinationFilename).length()+"p");

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