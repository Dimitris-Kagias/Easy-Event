package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class CustomerSupportActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_support);
    }

    public void openFAQ(View view) {
        Intent intent = new Intent(this, FAQActivity.class);
        startActivity(intent);
    }

    public void openTechnicalSupport(View view) {
        Intent intent = new Intent(this, TechnicalSupportActivity.class);
        startActivity(intent);
    }
}
