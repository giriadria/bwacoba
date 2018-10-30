package com.example.joe.bwa;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowImagesActivity extends AppCompatActivity  {
    //recyclerview object
    private RecyclerView recyclerView;
    private Context context;
    //adapter object
    private RecyclerView.Adapter adapter;

    //database reference
    private DatabaseReference mDatabase;

    //progress dialog
    private ProgressDialog progressDialog;

    private List<User> users;
    //list to hold all the uploaded images
    private List<Upload> uploads;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_images);

if (!isNetworkAvaible()){
    AlertDialog.Builder CheckBuilder=new AlertDialog.Builder(ShowImagesActivity.this);
    CheckBuilder.setIcon(R.drawable.error);
    CheckBuilder.setTitle("Tidak Ada Koneksi");
    CheckBuilder.setMessage("Cek kembali koneksi internet anda !");
    CheckBuilder.setPositiveButton("Ulangi", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
        Intent n =getIntent();
        finish();
        startActivity(n);
        }
    });

    CheckBuilder.setNegativeButton("Keluar", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    });
    AlertDialog alert=CheckBuilder.create();
    alert.show();
}



        FloatingActionButton FloatingActionButton = (FloatingActionButton) findViewById(R.id.ButtonEvent);
        FloatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(ShowImagesActivity.this, MainActivity.class));
            }


        });


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));






        progressDialog = new ProgressDialog(this);

        uploads = new ArrayList<>();


        //displaying progress dialog while fetching images
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        mDatabase = FirebaseDatabase
                .getInstance()
                .getReference(Constants.UPLOADS_EVENT)
                ;



        //adding an event listener to fetch values
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog
                progressDialog.dismiss();

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setPushId(postSnapshot.getKey());


                    uploads.add(upload);
                }
                //creating adapter
                adapter = new MyAdapter(getApplicationContext(), uploads);

                //adding adapter to recyclerview
                recyclerView.setAdapter(adapter);




                }








            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);




        return true;


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.profile) {
            Intent intentProfile=new Intent(ShowImagesActivity.this,ProfileActivity.class);
            startActivity(intentProfile);
            return true;

        }
        if (id == R.id.info){
            Intent intentinfo=new Intent(ShowImagesActivity.this,AppInfo.class);
            startActivity(intentinfo);
        return true;

    }return true;
    }


    public boolean isNetworkAvaible() {


        ConnectivityManager connectivityManager  = (ConnectivityManager) ShowImagesActivity.this.getSystemService(ShowImagesActivity.this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo !=null;

    }
}


