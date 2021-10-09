package com.example.sostrackingapp;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;

import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EmergencyMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<PlaceResponse> placeResponseList;
    private String type;
    private String TAG = "PLACE";
    private double longitude, latitude;
    PlacesClient placesClient;
    float zoomLevel = 13.0f; //This goes up to 21
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<PlaceResponse> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Places.initialize(getApplicationContext(), "AIzaSyCcgAmTUTcrnaQb5BCFGr8lW0YwIIOjlMM");

        // Create a new PlacesClient instance
        placesClient = Places.createClient(this);

        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getStringExtra("type");
            longitude = intent.getDoubleExtra("longitude", 0);
            latitude = intent.getDoubleExtra("latitude", 0);
        } else {
            finish();
        }

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<PlaceResponse>();
//        pending = new ArrayList<Event>();

//        data.add(new Plan("s","s","s","s"));
        adapter = new NearbyPlaceAdapter(data, 3, this);

        recyclerView.setAdapter(adapter);

        fetchPlacesNearMe();

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
        // Add a marker in Sydney and move the camera
        LatLng your_location = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(your_location).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(your_location, zoomLevel));
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Destination"));
    }

    private void fetchPlacesNearMe() {

//        Task<PlaceLikelihoodBufferResponse> placeResult = null;
        placeResponseList = new ArrayList<>();

        placeResponseList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject o = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        String str_origin = "location=" + latitude + "," + longitude;
        // Radius
        String radius = "radius=3000";
        //types
        String mode = "types=" + type;
        String parameters = str_origin + "&" + radius + "&" + mode;
        stringBuilder.append(parameters);
        // Key
        stringBuilder.append("&key=AIzaSyCcgAmTUTcrnaQb5BCFGr8lW0YwIIOjlMM");
        Log.d("REQUEST", stringBuilder.toString());


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                stringBuilder.toString(),
                o,
                new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("EXAMPLE", "Register Response: " + response.toString());
                        JSONArray jsonArray = response.optJSONArray("results");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                JSONObject geometry = jsonObject.getJSONObject("geometry");
                                JSONObject location = geometry.getJSONObject("location");
                                setPlaceDetail(jsonObject.getString("place_id"));


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

//                            adapter.notifyDataSetChanged();

                    }
                },
                Throwable::printStackTrace) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        queue.add(jsonObjectRequest);
    }

    private void setPlaceDetail(String placeID) {
        final String placeId = placeID;

        PlaceResponse placeResponse = new PlaceResponse();
        // Specify the fields to return.
        final List<Place.Field> placeFields = Arrays.asList(
                Place.Field.NAME,
                Place.Field.PHONE_NUMBER,
                Place.Field.RATING,
                Place.Field.PHOTO_METADATAS,
                Place.Field.ADDRESS,
                Place.Field.UTC_OFFSET,
                Place.Field.OPENING_HOURS,
                Place.Field.USER_RATINGS_TOTAL,
                Place.Field.LAT_LNG,
                Place.Field.TYPES
        );

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Log.i("response", response.getPlace().toString());
            Place place = response.getPlace();
            placeResponse.setName(place.getName());
            placeResponse.setAddress(place.getAddress());
            placeResponse.setLatLng(place.getLatLng());

            placeResponse.setPhone(place.getPhoneNumber());
            if (place.getRating() == null) {
                placeResponse.setRating(0);
            } else {
                placeResponse.setRating(place.getRating());
            }
            if (place.isOpen() != null)
                placeResponse.setStatus(place.isOpen());
//            placeResponse.setType(place.getTypes());

            if(place.getUserRatingsTotal()!=null){
                placeResponse.setTotal_user_rating(place.getUserRatingsTotal());
            }

            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w(TAG, "No photo metadata.");
                return;
            }

            final PhotoMetadata photoMetadata = metadata.get(0);

            // Get the attribution text.
            final String attributions = photoMetadata.getAttributions();

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Optional.
                    .setMaxHeight(300) // Optional.
                    .build();

            Log.e(TAG, "Placeresponse: " + placeResponse.toString());

            addMarker(place.getLatLng(),place.getName());
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                placeResponse.setImage(bitmap);
                data.add(placeResponse);
                adapter.notifyDataSetChanged();
                Log.e(TAG, "Place not found: " + placeResponse.toString());

            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    final ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                    final int statusCode = apiException.getStatusCode();
                    // TODO: Handle error with given status code.
                }
            });
            Log.i(TAG, "Place found: " + place.getName());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });
    }

    private void addMarker(LatLng latLng, String tittle) {
        if(mMap!=null){
            mMap.addMarker(new MarkerOptions().position(latLng).title(tittle));
        }

    }
}