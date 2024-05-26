package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class AdministratorHomePageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator_homepage);
    }

    public void chooseConfirmBusiness(View view) {
        Intent intent = new Intent(this, ConfirmBusinessActivity.class);
        startActivity(intent);
    }


}
