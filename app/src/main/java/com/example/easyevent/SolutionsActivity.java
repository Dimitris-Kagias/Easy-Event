package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SolutionsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solutions);

        String question = getIntent().getStringExtra("question");

        try {
            InputStream is = getAssets().open("troubleshooter.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray jsonArray = new JSONArray(json);
            LinearLayout layout = findViewById(R.id.layout_solutions);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (obj.getString("question").equals(question)) {
                    JSONArray answers = obj.getJSONArray("answers");
                    for (int j = 0; j < answers.length(); j++) {
                        addTextView(layout, answers.getString(j));
                    }
                    break;
                }
            }

            Button backButton = new Button(this);
            backButton.setText("Πίσω");
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            layout.addView(backButton);
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
