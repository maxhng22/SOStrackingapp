package com.example.sostrackingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ContactProfileAdapter extends RecyclerView.Adapter<ContactProfileAdapter.MyViewHolder> {

    private ArrayList<Contact> dataSet;
    private int show_button = 1;
    private Activity activity;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference ref = firebaseDatabase.getReference();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private String currentUserID;
    private ValueEventListener a;

    String Mobile;
    String Message;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView phone;
        ImageView profile_pic;


//        ImageView imageViewIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.phone = (TextView) itemView.findViewById(R.id.phone);


            this.profile_pic = (ImageView) itemView.findViewById(R.id.image_view);


        }
    }

    public ContactProfileAdapter(ArrayList<Contact> data, int showbutton, Activity activity) {
        this.activity = activity;
        show_button = showbutton;
        this.dataSet = data;
    }

    @Override
    public ContactProfileAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_profile_cardview, parent, false);

//        view.setOnClickListener(ScheduleActivity.myOnClickListener);

        ContactProfileAdapter.MyViewHolder myViewHolder = new ContactProfileAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final ContactProfileAdapter.MyViewHolder holder, final int listPosition) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        TextView name = holder.name;
        TextView phone = holder.phone;
        ImageView profile = holder.profile_pic;

//        //set value into textview
        name.setText(dataSet.get(listPosition).getName());
        phone.setText(dataSet.get(listPosition).getPhone());

        profile.setImageResource(R.drawable.users);
        if (!String.valueOf(dataSet.get(listPosition).getUrl_image()).equals("")) {
            if (dataSet.get(listPosition).getBitmap() != null) {
                profile.setImageBitmap(dataSet.get(listPosition).getBitmap());
            } else {
                new ContactProfileAdapter.getAndSetImage(String.valueOf(dataSet.get(listPosition).getUrl_image()), holder, dataSet.get(listPosition)).execute();
            }
        }


    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    private class getAndSetImage extends AsyncTask<String, Void, Void> {

        Bitmap bmp;

        URL url;
        String urlString;
        ImageView profile;

        Contact recentHistory;

        public getAndSetImage(String url, final ContactProfileAdapter.MyViewHolder holder, Contact recentHistory) {
            urlString = url;
            profile = holder.profile_pic;
            this.recentHistory = recentHistory;
        }

        @Override
        protected Void doInBackground(String... voids) {


            //get image from url and set imageview
            try {
                url = new URL(urlString);
                recentHistory.setBitmap(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ContactProfileAdapter.this.notifyDataSetChanged();

        }
    }
}


