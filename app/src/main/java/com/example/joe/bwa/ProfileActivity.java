package com.example.joe.bwa;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class ProfileActivity extends AppCompatActivity {

    private ImageView userImage;
    private TextView noHp,namauser;
    private DatabaseReference mDatabase;
    private String url;
    private Button edit;
    private List<User> uploads;
    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        userImage=findViewById(R.id.userView);
        noHp=findViewById(R.id.numberText);
        namauser=findViewById(R.id.namauserText);
        edit=findViewById(R.id.btnEdit);




        edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {




                    Intent prof = new Intent(ProfileActivity.this,SettingUser.class);

                 prof.putExtra("namaUser",namauser.getText().toString());


                    prof.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ProfileActivity.this.startActivity(prof);
                }



        });



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        String userPhone = user.getPhoneNumber();



        mDatabase = FirebaseDatabase
                .getInstance()
                .getReference("user").child(userId);

       mDatabase.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               namauser.setText(String.valueOf(dataSnapshot.child("namauser").getValue()));
               noHp.setText(String.valueOf(dataSnapshot.child("phone").getValue()));
               url = String.valueOf((dataSnapshot.child("url").getValue()));
               Glide.with(ProfileActivity.this).load(url).into(userImage);

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });




    }}

