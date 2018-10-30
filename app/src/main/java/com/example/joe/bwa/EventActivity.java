package com.example.joe.bwa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventActivity extends AppCompatActivity {

    private TextView textLokasi;
    private TextView textDeskripsi;
    private TextView textTanggal;
    private TextView textJam;
    private String URL;
    private ImageView ImageEvent2;
    private Button btnRate;
    RatingBar ratingBar;
    private TextView txtRatingValue;
    private List<Upload> uploads;
    private DatabaseReference mDatabase;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);


        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.EVENT_RATING).child("rating");

        btnRate=findViewById(R.id.btnRate);
        txtRatingValue=findViewById(R.id.txtRatingValue);
        ratingBar = (RatingBar) findViewById(R.id.rating);
       // ratingBar.setNumStars(5);
        ImageEvent2 = findViewById(R.id.imageEvent2);
        textLokasi = (TextView) findViewById(R.id.textLokasi);
        textDeskripsi = (TextView) findViewById(R.id.textDeskripsi);
        textTanggal = (TextView) findViewById(R.id.textTanggal);
        textJam = (TextView) findViewById(R.id.textJam);
        Intent event = getIntent();

        textLokasi.setText(event.getStringExtra("lokasi"));
        textDeskripsi.setText(event.getStringExtra("tentang"));
        textTanggal.setText(event.getStringExtra("tanggal"));
        textJam.setText(event.getStringExtra("jam"));
        URL = event.getStringExtra("url");
        Toast.makeText(this, event.getStringExtra("pushId"), Toast.LENGTH_LONG).show();
        Glide.with(this).load(URL).into(ImageEvent2);


        btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating();
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {



            @Override
            public void onRatingChanged(RatingBar ratingBar, float getrating, boolean fromUser) {

                float getrating2 = ratingBar.getRating();
                txtRatingValue.setText("Rating: "+getrating2);
            }
        });





        ImageEvent2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gambar=new Intent(EventActivity.this,GambarBesar.class);
                gambar.putExtra("url",URL);
                gambar.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                EventActivity.this.startActivity(gambar);

            }});






    }

    private void rating() {
        //checking if file is available

            //adding the file to reference


                            //displaying success toast
                            Toast.makeText(EventActivity.this, "RATE ", Toast.LENGTH_LONG).show();
                            sharedPref = getPreferences(MODE_PRIVATE);
                            //creating the upload object to store uploaded image details



                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = user.getUid();

                            String getrating = String.valueOf(ratingBar.getRating());
                            Intent event = getIntent();
                            String pushId= event.getStringExtra("pushId");
                            DatabaseReference eventRef = FirebaseDatabase
                                    .getInstance()
                                    .getReference(Constants.EVENT_RATING).child(pushId+" + "+userId);





                            Map<String, String> uploads = new HashMap<>();

                            uploads.put("uid",userId);
                            uploads.put("rating",getrating);
                            uploads.put("pushId",pushId);



                            eventRef.setValue(uploads);


                           /*DatabaseReference pushRef = eventRef.push();
                           String pushId = pushRef.getKey();
                           upload.setPushId(pushId);*/


                            sharedPref = getPreferences(MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            //editor.putString("userId", userId);
                            editor.commit();
                            // mDatabase.child(userId).setValue(upload);

                            //adding an upload to firebase database
                            // String uploadId = mDatabase.push().getKey();




                        }
                    }








