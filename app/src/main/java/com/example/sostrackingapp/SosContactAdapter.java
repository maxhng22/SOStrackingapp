package com.example.sostrackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SosContactAdapter  extends RecyclerView.Adapter<SosContactAdapter.MyViewHolder> {

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

        Button send,delete;




//        ImageView imageViewIcon;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.name= (TextView) itemView.findViewById(R.id.name);
            this.phone = (TextView) itemView.findViewById(R.id.phone);


            this.profile_pic = (ImageView) itemView.findViewById(R.id.image_view);

            this.delete = (Button) itemView.findViewById(R.id.delete);
            this.send = (Button) itemView.findViewById(R.id.send);


        }
    }

    public SosContactAdapter(ArrayList<Contact> data, int showbutton, Activity activity) {
        this.activity = activity;
        show_button = showbutton;
        this.dataSet = data;
    }

    @Override
    public SosContactAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_sos_contact_adapter, parent, false);

//        view.setOnClickListener(ScheduleActivity.myOnClickListener);

        SosContactAdapter.MyViewHolder myViewHolder = new SosContactAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final SosContactAdapter.MyViewHolder holder, final int listPosition) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        TextView name = holder.name;
        TextView phone = holder.phone;


        ImageView profile=holder.profile_pic;



        Button send=holder.send;
        Button delete=holder.delete;


//        //set value into textview
        name.setText(dataSet.get(listPosition).getName());
        phone.setText(dataSet.get(listPosition).getPhone());

        profile.setImageResource(R.drawable.users);
        if(!String.valueOf(dataSet.get(listPosition).getUrl_image()).equals("")){
            if(dataSet.get(listPosition).getBitmap() != null) {
                profile.setImageBitmap(dataSet.get(listPosition).getBitmap());
            } else {
                new getAndSetImage(String.valueOf(dataSet.get(listPosition).getUrl_image()), holder, dataSet.get(listPosition)).execute();
            }
        }
//        imageView.setImageResource(dataSet.get(listPosition).getImage());


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Are you sure you want to send SOS message to this contact?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String lat="", longtitude="";
                                Mobile=dataSet.get(listPosition).getPhone();
                                if(HomeActivity.homeActivity!=null){
                                    lat=String.valueOf(HomeActivity.homeActivity.getLatitude());
                                    longtitude=String.valueOf(HomeActivity.homeActivity.getLongitude());
                                }


                                String location_url="https://www.google.com/maps/search/?api=1&query="+lat+","+longtitude;

                                if(SosActivity.sosActivity!=null){
                                    Message=SosActivity.sosActivity.getExist_message()+" My location at:";
                                }

                                if(Message==null||Message.equals("")){
                                    Message="Hi! I need Help!! My location at:";
                                }


                                if(lat.equals("")||longtitude.equals("")){
                                    Message+="";
                                }else{
                                    Message+=location_url;
                                }

                                if(SosActivity.sosActivity!=null){
                                    SosActivity.sosActivity.sendNotification(Mobile, Message);
                                }

                                try{
                                    SmsManager smgr = SmsManager.getDefault();
                                    smgr.sendTextMessage(Mobile,null,Message,null,null);
                                    Toast.makeText(v.getContext(), "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e){
                                    Toast.makeText(v.getContext(), "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                                }
                            }


//                                try{
//                                    Intent i = new Intent(Intent.ACTION_VIEW);
//                                    i.setData(Uri.parse("smsto:"));
//                                    i.setType("vnd.android-dir/mms-sms");
//                                    i.putExtra("address", Mobile);
//                                    i.putExtra("sms_body",Message);
//                                    v.getContext().startActivity(Intent.createChooser(i, "Send sms via:"));
//                                }
//                                catch(Exception e) {
//                                    Toast.makeText(v.getContext(), "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
//
//
//                                }
//                            }

                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }

            ;
        });



        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Are you sure you want to delete this contact?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ref.child(user.getUid()).child("Contact_Information").child(dataSet.get(listPosition).getKey()).removeValue();
                            }


//                            }

                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }

            ;
        });

    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    private class getAndSetImage extends AsyncTask<String,Void,Void> {

        Bitmap bmp;

        URL url;
        String urlString;
        ImageView profile;

        Contact recentHistory;

        public getAndSetImage(String url, final SosContactAdapter.MyViewHolder holder, Contact recentHistory) {
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
            SosContactAdapter.this.notifyDataSetChanged();
//            if(bmp!=null) {
//                profile.setImageBitmap(bmp);
//            }else{
//                profile.setImageResource(R.drawable.user);
//            }
        }
    }


}