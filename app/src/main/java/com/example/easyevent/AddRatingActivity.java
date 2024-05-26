package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;

public class AddRatingActivity extends AppCompatActivity {

    private Spinner spinnerStars;
    private EditText edtReview;
    private static final String TAG = "AddRatingActivity";
    private static final int REQUEST_CODE_REVIEW = 1;
    private static final int REQUEST_CODE_VALIDATION_ERROR = 2;
// as
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rating);

        spinnerStars = findViewById(R.id.spinnerStars);
        edtReview = findViewById(R.id.edtReview);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        // Populate spinner with numbers 1-5
        Integer[] items = new Integer[]{1, 2, 3, 4, 5};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        spinnerStars.setAdapter(adapter);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview();
            }
        });
    }

    private void submitReview() {
        int stars = (int) spinnerStars.getSelectedItem();
        String reviewText = edtReview.getText().toString();

        if (reviewText.split("\\s+").length < 10 || reviewText.split("\\s+").length > 300) {
            Intent intent = new Intent(AddRatingActivity.this, MessageActivity.class);
            intent.putExtra("MESSAGE", "Το σχόλιο πρέπει να περιέχει περισσότερες από 10 λέξεις και λιγότερες από 300.");
            startActivityForResult(intent, REQUEST_CODE_VALIDATION_ERROR);
        } else {
            try {
                File file = new File(getFilesDir(), "business.json");
                if (!file.exists()) {
                    throw new Exception("Το αρχείο business.json δεν βρέθηκε.");
                }

                InputStream is = new FileInputStream(file);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String json = new String(buffer, "UTF-8");

                JSONArray jsonArray = new JSONArray(json);
                String businessName = getIntent().getStringExtra("business");

                boolean reviewAdded = false;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    if (obj.getString("name").equals(businessName)) {
                        JSONArray reviews = obj.getJSONArray("reviews");
                        JSONObject newReview = new JSONObject();
                        newReview.put("username", "user");
                        newReview.put("rating", stars);
                        newReview.put("comment", reviewText);
                        reviews.put(newReview);
                        obj.put("reviews", reviews);
                        reviewAdded = true;
                        break;
                    }
                }

                if (reviewAdded) {
                    // Write updated JSON to file
                    FileOutputStream fos = new FileOutputStream(file);
                    OutputStreamWriter osw = new OutputStreamWriter(fos);
                    osw.write(jsonArray.toString());
                    osw.close();

                    Log.d(TAG, "Review added: " + jsonArray.toString());

                    Intent intent = new Intent(AddRatingActivity.this, MessageActivity.class);
                    intent.putExtra("MESSAGE", "Επιτυχής καταχώρηση αξιολόγησης");
                    startActivityForResult(intent, REQUEST_CODE_REVIEW);
                } else {
                    throw new Exception("Η επιχείρηση δεν βρέθηκε στο αρχείο.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Σφάλμα κατά την αποθήκευση της αξιολόγησης", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error saving review", e);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REVIEW) {
            Intent intent = new Intent(this, CustomerHomePageActivity.class);
            startActivity(intent);
            finish();
        } else if (requestCode == REQUEST_CODE_VALIDATION_ERROR) {
            // Return to the AddRatingActivity without finishing it
            // No action needed, simply let the user correct their input
        }
    }
}
