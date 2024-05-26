package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Button;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Businesses2Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_businesses2);

        String businessType = getIntent().getStringExtra("businessType");

        try {
            InputStream is = getAssets().open("business.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray jsonArray = new JSONArray(json);
            LinearLayout layout = findViewById(R.id.layout_businesses);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (obj.getBoolean("state") && obj.getString("businessType").equals(businessType)) {
                    String name = obj.getString("name");
                    Button button = new Button(this);
                    button.setText(name);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Businesses2Activity.this, BusinessInfo2Activity.class);
                            intent.putExtra("businessName", name);
                            startActivity(intent);
                        }
                    });
                    layout.addView(button);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
