package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class CustomerHomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home_page);
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

    public void openCart(View view) {
        Intent intent = new Intent(this, BuyingCartActivity.class);
        startActivity(intent);
    }

    public void openCancel(View view) {
        Intent intent = new Intent(this, CancelActivity.class);
        startActivity(intent);
    }

    public void rateBusiness(View view) {
        Intent intent = new Intent(this, RateBusinessActivity.class);
        startActivity(intent);
    }

    public void openCustomerSupport(View view) {
        Intent intent = new Intent(this, CustomerSupportActivity.class);
        startActivity(intent);
    }
}
