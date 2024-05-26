package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
    }

    public void chooseCreateEvent(View view) {
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
    }

    public void chooseOffer(View view) {
        // Προσθέστε την κατάλληλη λειτουργικότητα εδώ
    }

    public void choosePackage(View view) {
        // Προσθέστε την κατάλληλη λειτουργικότητα εδώ
    }
}
