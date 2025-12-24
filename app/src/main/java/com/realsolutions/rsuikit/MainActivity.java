package com.realsolutions.rsuikit;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.realsolutions.rsuikit.R;
import com.realsolutions.uikit.RSButton;
import com.realsolutions.uikit.RSCheckBox;

import com.realsolutions.uikit.RSButton;
import com.realsolutions.uikit.RSEditText;
import com.realsolutions.uikit.RSPasswordInput;

public class MainActivity extends AppCompatActivity {

    RSEditText rsEditText;
    RSButton btnPrimary, btnSecondary;
    RSPasswordInput inpPass;
    RSCheckBox check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rsEditText = findViewById(R.id.inpSicil);
        inpPass = findViewById(R.id.inpPass);
        btnPrimary = findViewById(R.id.btnPrimary);
        btnSecondary = findViewById(R.id.btnSecondary);
        check = findViewById(R.id.checkBox);

        btnPrimary.setOnClickListener(view -> {
            rsEditText.setErrorState(null);
            inpPass.setErrorState();
        });

        btnSecondary.setOnClickListener(view -> {
            rsEditText.setSuccessState();
            inpPass.setSuccessState();
        });

        check.setOnClickListener(view -> {
            if (check.getStatus() == RSCheckBox.STATUS_CHECKED){
                Toast.makeText(this, "Checked", Toast.LENGTH_SHORT).show();
                check.setChecked(false);
            } else {
                Toast.makeText(this, "Unchecked", Toast.LENGTH_SHORT).show();
                check.setIndeterminate();
            }
        });
    }
}