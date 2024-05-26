package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class BusinessInfo2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_info2);

        String businessName = getIntent().getStringExtra("businessName");

        try {
            InputStream is = getAssets().open("business.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray jsonArray = new JSONArray(json);
            LinearLayout layout = findViewById(R.id.layout_business_info);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (obj.getBoolean("state") && obj.getString("name").equals(businessName)) {
                    addTextView(layout, "Name: " + obj.getString("name"));
                    addTextView(layout, "Business Type: " + obj.getString("businessType"));
                    addTextView(layout, "Event Types: " + obj.getJSONArray("eventTypes").join(", "));
                    addTextView(layout, "Location: " + obj.getString("location"));
                    addTextView(layout, "Contact Info: " + obj.getString("contactInfo"));
                    addTextView(layout, "Services: " + obj.getJSONArray("services").join(", "));

                    Button backButton = new Button(this);
                    backButton.setText("Πίσω");
                    backButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                    layout.addView(backButton);

                    Button requestButton = new Button(this);
                    requestButton.setText("Αποστολή Αιτήματος");
                    requestButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(BusinessInfo2Activity.this, BusinessHomePageActivity.class);
                            startActivity(intent);
                        }
                    });
                    layout.addView(requestButton);

                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addTextView(LinearLayout layout, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        layout.addView(textView);
    }
}
