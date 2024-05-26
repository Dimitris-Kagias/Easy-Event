package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CreateOfferActivity extends AppCompatActivity {

    private EditText editTextAvailability;
    private EditText editTextLocation;
    private EditText editTextDescription;

    private String username;
    private GetBusiness business;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);

        editTextAvailability = findViewById(R.id.editText_availability);
        editTextLocation = findViewById(R.id.editText_location);
        editTextDescription = findViewById(R.id.editText_description);

        username = getIntent().getStringExtra("username");

        loadBusinessDetails();

        Button buttonSubmit = findViewById(R.id.button_submit_offer);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOffer();
            }
        });
    }

    private void loadBusinessDetails() {
        try {
            File file = new File(getFilesDir(), "business.json");
            InputStream is = new FileInputStream(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<GetBusiness>>() {}.getType();
            List<GetBusiness> businesses = gson.fromJson(json, listType);

            for (GetBusiness b : businesses) {
                if (b.getUsername().equals(username)) {
                    business = b;
                    break;
                }
            }

            if (business != null && !business.isState()) {
                showMessage("Δεν έχετε ακόμα δικαίωμα υποβολής προσφοράς έως ότου να επικαιροποιηθούν τα διαπιστευτήρια σας", true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Σφάλμα κατά την ανάγνωση των δεδομένων", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitOffer() {
        String availability = editTextAvailability.getText().toString();
        String location = editTextLocation.getText().toString();
        String description = editTextDescription.getText().toString();

        // Έλεγχος μορφής ημερομηνίας
        String datePattern = "\\d{2}/\\d{2}/\\d{4}-\\d{2}/\\d{2}/\\d{4}";
        if (availability.isEmpty() || location.isEmpty() || description.isEmpty() || !Pattern.matches(datePattern, availability)) {
            showMessage("Παρακαλώ συμπληρώστε σωστά όλα τα πεδία", false);
        } else {
            Intent intent = new Intent(this, ConfirmActivity.class);
            intent.putExtra("offer", true); // Προσθήκη της πληροφορίας για την προσφορά
            intent.putExtra("MESSAGE", "Ολοκλήρωση δημιουργίας προσφοράς;");
            intent.putExtra("availability", availability);
            intent.putExtra("location", location);
            intent.putExtra("description", description);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // Κλήση της υπερκλάσης
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getBooleanExtra("CONFIRMED", false)) {
                    saveOffer(data.getStringExtra("availability"), data.getStringExtra("location"), data.getStringExtra("description"));
                } else {
                    startActivity(new Intent(this, BusinessHomePageActivity.class));
                    finish();
                }
            }
        } else if (requestCode == 2) {
            // Επιστροφή στη φόρμα αν υπήρξε σφάλμα συμπλήρωσης πεδίων
        }
    }

    private void saveOffer(String availability, String location, String description) {
        try {
            File file = new File(getFilesDir(), "offer.json");
            List<Offer> offers;

            if (file.exists()) {
                InputStream is = new FileInputStream(file);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String json = new String(buffer, "UTF-8");

                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<Offer>>() {}.getType();
                offers = gson.fromJson(json, listType);
            } else {
                offers = new ArrayList<>();
            }

            Offer newOffer = new Offer(business.getName(), availability, location, description);
            offers.add(newOffer);

            Gson gson = new Gson();
            String json = gson.toJson(offers);

            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(json);
            osw.close();

            Toast.makeText(this, "Η προσφορά προστέθηκε με επιτυχία", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, BusinessHomePageActivity.class));
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Σφάλμα κατά την αποθήκευση της προσφοράς", Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessage(String message, boolean goToHomePage) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("MESSAGE", message);
        startActivityForResult(intent, goToHomePage ? 1 : 2);
    }
}
