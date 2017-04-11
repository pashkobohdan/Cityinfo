package com.pashkobohdan.cityinfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FullInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_info);

        Intent intent = getIntent();
        String cityName = intent.getStringExtra("city");
        String countryName = intent.getStringExtra("country");



    }
}
