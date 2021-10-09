package com.example.sostrackingapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.cardemulation.CardEmulation;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    private ArrayList<Contact> dataSet;
    private Activity activity;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView user_name;
        CardView card_view;
        TextView phone_no;
        ImageView profile_pic;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.user_name = itemView.findViewById(R.id.user_name);
            this.phone_no = itemView.findViewById(R.id.phone_no);
            this.card_view = itemView.findViewById(R.id.card_view);
            this.profile_pic=itemView.findViewById(R.id.imageView);

        }
    }

    public ContactAdapter(Activity activity, ArrayList<Contact> data) {
        this.dataSet = data;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_card, parent, false);

//        view.setOnClickListener(ScheduleActivity.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {


        TextView user_name = holder.user_name;
        TextView phone_no = holder.phone_no;
        ImageView profile=holder.profile_pic;
        CardView card_view=holder.card_view;

        //set all value into textview
        user_name.setText(dataSet.get(listPosition).getName());
        phone_no.setText(String.valueOf(dataSet.get(listPosition).getPhone()));



        //send coach info into next activity and go into booking activity
        card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ContactTrackingActivity.class);
                intent.putExtra("phone",dataSet.get(listPosition).getPhone());
                intent.putExtra("name", dataSet.get(listPosition).getName());
//                intent.putExtra("fee", dataSet.get(listPosition).getCoach_fees());//
                activity.startActivity(intent);

            }
        });



        //set imageview
        profile.setImageResource(R.drawable.users);
        if(!String.valueOf(dataSet.get(listPosition).getUrl_image()).equals("")){
            if(dataSet.get(listPosition).getBitmap() != null) {
                profile.setImageBitmap(dataSet.get(listPosition).getBitmap());
            } else {
                new getAndSetImage(String.valueOf(dataSet.get(listPosition).getUrl_image()), holder, dataSet.get(listPosition)).execute();
            }
        }
//        imageView.setImageResource(dataSet.get(listPosition).getImage());
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

        public getAndSetImage(String url, final ContactAdapter.MyViewHolder holder, Contact recentHistory){
            urlString = url;
            profile=holder.profile_pic;
            this.recentHistory = recentHistory;
        }

        @Override
        protected Void doInBackground(String... voids) {


            //get image from url and set imageview
            try {
                url = new URL(urlString);
                recentHistory.setBitmap(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
            }catch(MalformedURLException e)
            {
                e.printStackTrace();

            }catch(IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ContactAdapter.this.notifyDataSetChanged();
//            if(bmp!=null) {
//                profile.setImageBitmap(bmp);
//            }else{
//                profile.setImageResource(R.drawable.user);
//            }
        }
    }
}
