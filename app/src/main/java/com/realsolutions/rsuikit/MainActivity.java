package com.realsolutions.rsuikit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.realsolutions.rsuikit.R;
import com.realsolutions.uikit.RSButton;
import com.realsolutions.uikit.RSCheckBox;
import com.realsolutions.uikit.RSCallout;
import com.realsolutions.uikit.RSEditText;
import com.realsolutions.uikit.RSPasswordInput;
import com.realsolutions.uikit.RSToast;
import com.realsolutions.uikit.RSToastType;

public class MainActivity extends AppCompatActivity {

    RSEditText rsEditText;
    RSButton btnPrimary, btnSecondary;
    RSPasswordInput inpPass;
    RSCheckBox check;

    // RSCallout örnekleri
    RSCallout calloutInfo, calloutSuccess, calloutWarning, calloutError;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rsEditText = findViewById(R.id.inpSicil);
        inpPass = findViewById(R.id.inpPass);
        btnPrimary = findViewById(R.id.btnPrimary);
        btnSecondary = findViewById(R.id.btnSecondary);
        check = findViewById(R.id.checkBox);

        // RSCallout binding
        calloutInfo = findViewById(R.id.calloutInfo);
        calloutSuccess = findViewById(R.id.calloutSuccess);

        btnPrimary.setOnClickListener(view -> {
            rsEditText.setErrorState(null);
            inpPass.setErrorState();
        });

        btnSecondary.setOnClickListener(view -> {
            rsEditText.setSuccessState();
            inpPass.setSuccessState();
            RSToast.show(
                    findViewById(android.R.id.content),
                    "Kod okunamadı. Lütfen tekrar okutunuz.", RSToastType.INFO
            );
        });

        check.setOnClickListener(view -> {
            if (check.getStatus() == RSCheckBox.STATUS_CHECKED) {
                Toast.makeText(this, "Checked", Toast.LENGTH_SHORT).show();
                check.setChecked(false);
            } else {
                Toast.makeText(this, "Unchecked", Toast.LENGTH_SHORT).show();
                check.setIndeterminate();
            }
        });

        // RSCallout Action Click Listeners
        calloutInfo.setOnActionClickListener(v -> {
            Toast.makeText(this, "Info: Daha fazla tıklandı!", Toast.LENGTH_SHORT).show();
        });

        calloutSuccess.setOnActionClickListener(v -> {
            Toast.makeText(this, "Success: Tamam tıklandı!", Toast.LENGTH_SHORT).show();
            // Örnek: Callout'u gizle
            // calloutSuccess.setVisibility(View.GONE);
        });

//         Programatik değişiklik örneği
//         calloutWarning.setMessage("Yeni uyarı mesajı");
//         calloutError.setType(RSCallout.TYPE_INFO
    }
}
