package com.example.easyevent;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        TextView textViewMessage = findViewById(R.id.textView_message);
        String message = getIntent().getStringExtra("MESSAGE");
        textViewMessage.setText(message);
    }

    public void closeMessage(View view) {
        setResult(RESULT_OK);
        finish();
    }
}
