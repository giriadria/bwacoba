package com.example.joe.bwa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GambarBesar extends AppCompatActivity {

    private ImageView imageBig;
    private String URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gambar_besar);


        imageBig=findViewById(R.id.imageBig);
        Intent gambar = getIntent();

        URL = gambar.getStringExtra("url");
        Glide.with(this).load(URL).into(imageBig);
    }
}
