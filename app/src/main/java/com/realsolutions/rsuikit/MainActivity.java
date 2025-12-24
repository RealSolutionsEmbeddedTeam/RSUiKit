package com.realsolutions.rsuikit;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.realsolutions.uikit.RSCheckBox;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Interactive checkbox demo
        RSCheckBox interactiveCheckbox = findViewById(R.id.checkbox_interactive);
        TextView statusText = findViewById(R.id.checkbox_status);

        if (interactiveCheckbox != null && statusText != null) {
            interactiveCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                statusText.setText("Durum: " + (isChecked ? "Checked ✓" : "Unchecked"));
            });
        }
    }
}