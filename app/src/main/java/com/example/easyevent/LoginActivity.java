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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Φόρτωση δεδομένων από το αρχείο assets στο αρχείο του κινητού
        loadBusinessDataFromAssets();

        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                List<User> users = loadUsersFromJson();
                if (users == null) {
                    Toast.makeText(LoginActivity.this, "Error loading user data", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean validUser = false;

                for (User user : users) {
                    if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                        validUser = true;
                        Intent intent;
                        switch (user.getRole()) {
                            case "Customer":
                                intent = new Intent(LoginActivity.this, CustomerHomePageActivity.class);
                                startActivity(intent);
                                break;
                            case "Business":
                                intent = new Intent(LoginActivity.this, BusinessHomePageActivity.class);
                                intent.putExtra("username", username);
                                startActivity(intent);
                                break;
                            case "Administrator":
                                intent = new Intent(LoginActivity.this, AdministratorHomePageActivity.class);
                                startActivity(intent);
                                break;
                        }
                        break;
                    }
                }

                if (!validUser) {
                    Toast.makeText(LoginActivity.this, "Invalid login credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private List<User> loadUsersFromJson() {
        String json = null;
        try {
            InputStream is = getAssets().open("user.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Log.d(TAG, "user.json content: " + json); // Logging the content of the JSON file
        } catch (IOException ex) {
            Log.e(TAG, "Error reading user.json", ex);
            return null;
        }

        Gson gson = new Gson();
        Type userListType = new TypeToken<List<User>>(){}.getType();
        return gson.fromJson(json, userListType);
    }

    private void loadBusinessDataFromAssets() {
        try {
            // Διαβάζουμε τα δεδομένα από το αρχείο assets
            InputStream is = getAssets().open("business.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            // Αποθηκεύουμε τα δεδομένα στο αρχείο του κινητού
            File file = new File(getFilesDir(), "business.json");

            // Αδειάζουμε το αρχείο αν περιέχει δεδομένα
            if (file.exists()) {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(new byte[0]);
                fos.close();
            }

            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(json);
            osw.close();

            Log.d(TAG, "Loaded data from assets to local business.json");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Σφάλμα κατά τη φόρτωση των δεδομένων", Toast.LENGTH_SHORT).show();
        }
    }
}
