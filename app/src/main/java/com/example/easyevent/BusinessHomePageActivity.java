package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class BusinessHomePageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_homepage);
    }

    public void chooseCreateOffer(View view) {
        Intent intent = new Intent(this, CreateOfferActivity.class);
        String username = getIntent().getStringExtra("username");
        intent.putExtra("username", username);
        startActivity(intent);
    }

    public void chooseCreatePackage(View view) {
        Intent intent = new Intent(this, CreatePackageActivity.class);
        String username = getIntent().getStringExtra("username");
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
