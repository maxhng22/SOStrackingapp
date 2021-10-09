package com.example.sostrackingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class AddContactActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ImageView profile;
    private final int PICK_IMAGE_REQUEST = 71;

    private ValueEventListener a;
    private EditText edit_name, edit_phone;

    Bitmap selectedImage;
    InputStream imageStream;
    private Uri fileImage;

    StorageReference storageReference;
    FirebaseStorage storage;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        edit_name = findViewById(R.id.name);
        edit_phone = findViewById(R.id.phone_no);
        imageView = findViewById(R.id.imageView);

        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.imageView).setOnClickListener(this);
        findViewById(R.id.add).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.add:

                String name = edit_name.getText().toString().trim();
                String phone = edit_phone.getText().toString().trim();

                //validation to check if username textview is empty
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                //validation to check if user age textview is empty
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), "Phone Number cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }


                checkPhone(value -> {
                    if (value) {
                        final Contact updated_contact = new Contact();
                        updated_contact.setName(name);
                        updated_contact.setPhone(phone);


                        if (fileImage != null && selectedImage != null) {
                            StorageReference stoRefImage = storageReference.child("image/" + UUID.randomUUID().toString());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            stoRefImage.putBytes(data)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!urlTask.isSuccessful()) ;
                                            Uri downloadUrl = urlTask.getResult();
                                            if (downloadUrl != null) {
                                                String urlimage = downloadUrl.toString();
                                                updated_contact.setUrl_image(urlimage);

                                                ref.child(user.getUid()).child("Contact_Information").push().setValue(updated_contact, new DatabaseReference.CompletionListener() {
                                                    public void onComplete(DatabaseError error, @NonNull DatabaseReference ref) {
                                                        Toast.makeText(getApplicationContext(), "Add contact successfully", Toast.LENGTH_SHORT).show();
//                                                        Intent intent = new Intent(AddContactActivity.this, SosActivity.class);
//                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });

                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddContactActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                                    .getTotalByteCount());
                                        }
                                    });

                        } else {

                            ref.child(user.getUid()).child("Contact_Information").push().setValue(updated_contact, new DatabaseReference.CompletionListener() {
                                public void onComplete(DatabaseError error, @NonNull DatabaseReference ref) {
                                    Toast.makeText(getApplicationContext(), "Add contact successfully", Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(AddContactActivity.this, SosActivity.class);
//                                    startActivity(intent);
                                    finish();
                                }
                            });

                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Not exist!!", Toast.LENGTH_SHORT).show();
                    }
                }, phone);

                break;
            case R.id.back:
                finish();
                break;
            case R.id.imageView:
                chooseImage();
                break;
        }

    }

    public interface MyCallback {
        void onCallback(Boolean value);
    }


    public void checkPhone(MyCallback myCallback, String phone) {

        a = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    myCallback.onCallback(true);
                } else {
                    myCallback.onCallback(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };


        ref.orderByChild("User_Information/phone").equalTo(phone).addListenerForSingleValueEvent(a);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE_REQUEST:
                if (resultCode == Activity.RESULT_OK
                        && data != null && data.getData() != null) {
                    fileImage = data.getData();

                    try {
                        selectedImage = getBitmap(fileImage);
//                        imageStream = getContentResolver().openInputStream(fileImage);
//                        selectedImage = BitmapFactory.decodeStream(imageStream);

                        imageView.setImageBitmap(selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //choose image from gallery
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private class getAndSetImage extends AsyncTask<String, Void, Void> {

        Bitmap bmp;
        URL url;
        String urlString;

        public getAndSetImage(String url) {
            urlString = url;
        }

        @Override
        protected Void doInBackground(String... voids) {
            try {
                url = new URL(urlString);
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

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
            if (bmp != null) {
                profile.setImageBitmap(bmp);
            }
        }
    }

    private Bitmap getBitmap(Uri uri) throws IOException {

        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 480000; // 1.2MP
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();


            int scale = 1;
            while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }

            Bitmap resultBitmap = null;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                options = new BitmapFactory.Options();
                options.inSampleSize = scale;
                resultBitmap = BitmapFactory.decodeStream(in, null, options);

                // resize to desired dimensions
                int height = resultBitmap.getHeight();
                int width = resultBitmap.getWidth();
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, (int) x,
                        (int) y, true);
                resultBitmap.recycle();
                resultBitmap = scaledBitmap;

                System.gc();
            } else {
                resultBitmap = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("", "bitmap size - width: " + resultBitmap.getWidth() + ", height: " + resultBitmap.getHeight());
            return resultBitmap;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
//            throw new Exception("");
//            return null;
            throw e;
        }
    }
}