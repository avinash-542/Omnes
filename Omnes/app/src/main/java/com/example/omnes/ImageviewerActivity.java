package com.example.omnes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageviewerActivity extends AppCompatActivity {
    private ImageView imageview;
    private String imageurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageviewer);
        imageview = findViewById(R.id.imageviewer);
        imageurl = getIntent().getStringExtra("Url");

        Picasso.get().load(imageurl).into(imageview);
    }
}