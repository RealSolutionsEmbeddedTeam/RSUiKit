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
        // Demo layout - all components shown statically
    }
}
