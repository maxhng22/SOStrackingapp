package com.example.sostrackingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class NearbyPlaceAdapter extends RecyclerView.Adapter<NearbyPlaceAdapter.MyViewHolder> {

    private ArrayList<PlaceResponse> dataSet;


    private Activity activity;
    private String name;
    private double rating;
    private String type;
    private Bitmap image;
    private String address;
    private String phone;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView address;
        TextView type;
        TextView status;
        TextView rating_num;
        TextView phone;
        RatingBar rating;
        ImageView profile_pic;


//        ImageView imageViewIcon;

        public MyViewHolder(View itemView) {

            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.name);
            this.address = (TextView) itemView.findViewById(R.id.address);
            this.rating = (RatingBar) itemView.findViewById(R.id.rating);
            this.rating_num = (TextView) itemView.findViewById(R.id.numberstar);
            this.status = (TextView) itemView.findViewById(R.id.status);
            this.type = (TextView) itemView.findViewById(R.id.number_of_review);
            this.phone = (TextView) itemView.findViewById(R.id.phone);
            this.profile_pic = (ImageView) itemView.findViewById(R.id.image_view);


        }
    }

    public NearbyPlaceAdapter(ArrayList<PlaceResponse> data, int showbutton, Activity activity) {

        Log.i("dwqdw","dwqdwqdwqdqw");
        this.activity = activity;
        this.dataSet = data;
    }

    @Override
    public NearbyPlaceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_detail_cardview, parent, false);

//        view.setOnClickListener(ScheduleActivity.myOnClickListener);

        NearbyPlaceAdapter.MyViewHolder myViewHolder = new NearbyPlaceAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final NearbyPlaceAdapter.MyViewHolder holder, final int listPosition) {

        Log.i("dwqdw","dwqdwqdwqdqw");
        TextView name = holder.name;
        TextView phone = holder.phone;
        TextView type = holder.type;
        TextView address = holder.address;
        TextView rating_num = holder.rating_num;
        TextView status = holder.status;
        RatingBar rating = holder.rating;
        ImageView profile = holder.profile_pic;



        name.setText(dataSet.get(listPosition).getName());
//        if(dataSet.get(listPosition).getPhone()!=null&&!dataSet.get(listPosition).getPhone().equals("")){
//            phone.setPadding(0,0,20,0);
//        }
        phone.setText(dataSet.get(listPosition).getPhone());
        type.setText("("+dataSet.get(listPosition).getTotal_user_rating()+")");
        address.setText(dataSet.get(listPosition).getAddress());
        rating.setRating((float) dataSet.get(listPosition).getRating());
        rating_num.setText(String.valueOf(dataSet.get(listPosition).getRating()));
        profile.setImageBitmap(dataSet.get(listPosition).getImage());
        if(dataSet.get(listPosition).isStatus()){
            status.setText("Open Now");
        }else{
            status.setText("Closed");
        }


//        //set value into textview
//        name.setText(dataSet.get(listPosition).getName());
//        phone.setText(dataSet.get(listPosition).getPhone());
//
//        profile.setImageResource(R.drawable.users);
//        if (!String.valueOf(dataSet.get(listPosition).getUrl_image()).equals("")) {
//            if (dataSet.get(listPosition).getBitmap() != null) {
//                profile.setImageBitmap(dataSet.get(listPosition).getBitmap());
//            } else {
//                new NearbyPlaceAdapter.getAndSetImage(String.valueOf(dataSet.get(listPosition).getUrl_image()), holder, dataSet.get(listPosition)).execute();
//            }
//        }
//        imageView.setImageResource(dataSet.get(listPosition).getImage());


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

        public getAndSetImage(String url, final NearbyPlaceAdapter.MyViewHolder holder, Contact recentHistory) {
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
            NearbyPlaceAdapter.this.notifyDataSetChanged();
//            if(bmp!=null) {
//                profile.setImageBitmap(bmp);
//            }else{
//                profile.setImageResource(R.drawable.user);
//            }
        }
    }
}


