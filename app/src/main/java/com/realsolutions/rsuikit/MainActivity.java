package com.realsolutions.rsuikit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.realsolutions.rsuikit.R;
import com.realsolutions.uikit.RSButton;
import com.realsolutions.uikit.RSCheckBox;
import com.realsolutions.uikit.RSCallout;
import com.realsolutions.uikit.RSDialog;
import com.realsolutions.uikit.RSEditText;
import com.realsolutions.uikit.RSLabel;
import com.realsolutions.uikit.RSPasswordInput;
import com.realsolutions.uikit.RSToast;
import com.realsolutions.uikit.RSToastType;
import com.realsolutions.uikit.navbar.RSNavBar;
import com.realsolutions.uikit.select.RSSelect;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RSLabel label = findViewById(R.id.myLabel);
        label.setOnInfoClickListener(v -> {
            RSToast.show(v, "Info clicked", RSToastType.INFO);
        });

        RSNavBar nav = findViewById(R.id.rsNavBar);
        nav.setItemWeights(1.0f, 1.7f);
        nav.setWeightAnimDuration(200);

        nav.setOnItemSelectedListener(itemId -> {
            if (itemId == R.id.nav_search) {
                // fragment replace / navigate
            } else if (itemId == R.id.nav_scan) {
            }
        });

        // programmatic select
        nav.selectItem(R.id.nav_scan, false);

        RSSelect sel = findViewById(R.id.rsSelectUser);

        sel.setItems(Arrays.asList("Ali Veli", "Turgay Hopal", "Ayşe Yılmaz"));


        sel.setOnItemSelectedListener((index, value) -> {
            // seçildi
        });

        // hide/show
        // nav.hide(true);
        // nav.show(true);

    }
}
