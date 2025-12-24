package com.realsolutions.rsuikit;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.realsolutions.rsuikit.R;
import com.realsolutions.uikit.RSButton;
import com.realsolutions.uikit.RSCheckBox;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.realsolutions.rsuikit.R.layout.activity_main);

        // Bileşenleri tanımla
        RSCheckBox cbInteractive = findViewById(com.realsolutions.rsuikit.R.id.cb_interactive);
        RSButton btnUnchecked = findViewById(R.id.btn_set_unchecked);
        RSButton btnChecked = findViewById(com.realsolutions.rsuikit.R.id.btn_set_checked);
        RSButton btnIndeterminate = findViewById(R.id.btn_set_indeterminate);

        // 1. Durum Dinleme (Listener)
        cbInteractive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Standart check işlemi yapıldığında burası çalışır
            // Ancak bizim 'status' değişkenimiz daha kapsamlıdır
            String statusName = "Değişti";
            Toast.makeText(this, "Check durumu: " + isChecked, Toast.LENGTH_SHORT).show();
        });

        // 2. Programatik Olarak Durum Atama
        btnUnchecked.setOnClickListener(v -> {
            cbInteractive.setStatus(RSCheckBox.STATUS_UNCHECKED);
        });

        btnChecked.setOnClickListener(v -> {
            cbInteractive.setStatus(RSCheckBox.STATUS_CHECKED);
        });

        btnIndeterminate.setOnClickListener(v -> {
            cbInteractive.setStatus(RSCheckBox.STATUS_INDETERMINATE);
        });

        // 3. Durumu Kontrol Etme
        cbInteractive.setOnClickListener(v -> {
            int currentStatus = cbInteractive.getStatus();
            if (currentStatus == RSCheckBox.STATUS_INDETERMINATE) {
                // Özel bir mantık çalıştırılabilir
            }
        });
    }
}