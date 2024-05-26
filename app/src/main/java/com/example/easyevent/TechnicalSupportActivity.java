package com.example.easyevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TechnicalSupportActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technical_support);
    }

    public void submitIssue(View view) {
        EditText editText = findViewById(R.id.editText_issue);
        String issue = editText.getText().toString();

        try {
            InputStream is = getAssets().open("questions.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONArray jsonArray = new JSONArray(json);

            JSONObject newIssue = new JSONObject();
            newIssue.put("question", issue);
            newIssue.put("answers", new JSONArray());

            jsonArray.put(newIssue);

            FileOutputStream fos = openFileOutput("questions.json", MODE_PRIVATE);
            fos.write(jsonArray.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("MESSAGE", "Το πρόβλημά σας καταχωρήθηκε επιτυχώς. Ο διαχειριστής θα επικοινωνήσει σύντομα μαζί σας!");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, CustomerHomePageActivity.class);
            startActivity(intent);
        }
    }
}
