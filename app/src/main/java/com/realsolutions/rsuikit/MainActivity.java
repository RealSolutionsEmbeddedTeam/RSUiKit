package com.realsolutions.rsuikit;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.realsolutions.uikit.RSButton;
import com.realsolutions.uikit.RSEditText;

public class MainActivity extends AppCompatActivity {

    RSEditText rsEditText;
    RSButton btnPrimary, btnSecondary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rsEditText = findViewById(R.id.inpSicil);
        btnPrimary = findViewById(R.id.btnPrimary);
        btnSecondary = findViewById(R.id.btnSecondary);

        btnPrimary.setOnClickListener(view -> {
            rsEditText.setErrorState(null);
        });

        btnSecondary.setOnClickListener(view -> {
            rsEditText.setSuccessState();
        });
    }
}