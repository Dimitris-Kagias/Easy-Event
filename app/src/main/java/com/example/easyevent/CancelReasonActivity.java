package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CancelReasonActivity extends AppCompatActivity {

    private CartItem cartItem;
    private EditText editTextReason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_reason);

        editTextReason = findViewById(R.id.editText_reason);
        Button submitButton = findViewById(R.id.button_submit);

        Intent intent = getIntent();
        cartItem = (CartItem) intent.getSerializableExtra("CANCEL_ITEM");

        if (cartItem == null) {
            Log.e("CancelReasonActivity", "CartItem is null in CancelReasonActivity!");
            Toast.makeText(this, "Error loading event details.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d("CancelReasonActivity", "Received cartItem: " + cartItem.getBusinessName());

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSubmitReason();
            }
        });
    }

    private void validateAndSubmitReason() {
        String reason = editTextReason.getText().toString().trim();
        String[] words = reason.split("\\s+");

        if (words.length < 10 || words.length > 50) {
            Intent intent = new Intent(this, MessageActivity.class);
            intent.putExtra("MESSAGE", "Παρακαλώ συμπληρώστε το πεδίο χρησιμοποιώντας περισσότερες από 10 και λιγότερες από 50 λέξεις.");
            startActivity(intent);
        } else {
            removePurchaseItem();
            showMessageAndContactInfo();
        }
    }

    private void removePurchaseItem() {
        File file = new File(getFilesDir(), "purchase.json");
        List<CartItem> purchaseList = new ArrayList<>();

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                Gson gson = new Gson();
                Type listType = new TypeToken<List<CartItem>>() {}.getType();
                purchaseList = gson.fromJson(jsonBuilder.toString(), listType);

                if (purchaseList != null) {
                    purchaseList.removeIf(item -> item.getBusinessName().equals(cartItem.getBusinessName())
                            && item.getDate().equals(cartItem.getDate())
                            && item.getTime().equals(cartItem.getTime())
                            && item.getLocation().equals(cartItem.getLocation()));
                }

                try (FileWriter writer = new FileWriter(file, false)) {
                    gson.toJson(purchaseList, writer);
                    writer.flush();
                }

            } catch (IOException e) {
                Log.e("CancelReasonActivity", "Error reading or writing purchase.json", e);
                Toast.makeText(this, "Error processing cancellation.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showMessageAndContactInfo() {
        String contactInfo = getContactInfo(cartItem.getBusinessName());
        String message = "Η ενέργειά σας ολοκληρώθηκε με επιτυχία. Η επιστροφή των χρημάτων σας θα γίνει κατόπιν συνεννόησης με την επιχείρηση.\n" + contactInfo;

        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("MESSAGE", message);
        startActivityForResult(intent, 1);
    }

    private String getContactInfo(String businessName) {
        // Αναζητήστε τα στοιχεία επικοινωνίας από το business.json
        try {
            InputStream is = getAssets().open("business.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            Type listType = new TypeToken<List<GetBusiness>>() {}.getType();
            List<GetBusiness> businesses = gson.fromJson(json, listType);

            for (GetBusiness business : businesses) {
                if (business.getName().equals(businessName)) {
                    return business.getContactInfo();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CancelReasonActivity", "Error reading business.json", e);
        }
        return "Στοιχεία επικοινωνίας δεν βρέθηκαν.";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Intent intent = new Intent(this, CustomerHomePageActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
