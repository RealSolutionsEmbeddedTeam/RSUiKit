package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.realsolutions.uikit.R;

public class RSHelperText extends LinearLayout {

    // RSState değerleri ile birebir
    public static final int STATE_NORMAL  = 0;
    public static final int STATE_FOCUSED = 1;
    public static final int STATE_ERROR   = 2;
    public static final int STATE_SUCCESS = 3;

    private View iconBox;
    private ImageView icon;

    private TextView text;

    private int rsState = STATE_NORMAL;
    private boolean rsEnabled = true;

    // Design tokens
    private int colorNormal;
    private int colorError;
    private int colorSuccess;
    private int colorDisabled;

    public RSHelperText(@NonNull Context context) {
        this(context, null);
    }

    public RSHelperText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);

        LayoutInflater.from(context).inflate(R.layout.rs_helper_text, this, true);

        iconBox = findViewById(R.id.rsHelperIconBox);
        icon = findViewById(R.id.rsHelperIcon);
        text = findViewById(R.id.rsHelperText);

        // RSUiKit token’ları
        colorNormal  = ContextCompat.getColor(context, R.color.rs_text_secondary);
        colorError   = ContextCompat.getColor(context, R.color.rs_text_error_solid);
        colorSuccess = ContextCompat.getColor(context, R.color.rs_text_success_solid);
        colorDisabled= ContextCompat.getColor(context, R.color.rs_text_tertiary);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RSHelperText);

            setText(a.getString(R.styleable.RSHelperText_rsText));

            rsState = a.getInt(R.styleable.RSHelperText_rsState, STATE_NORMAL);
            rsEnabled = a.getBoolean(R.styleable.RSHelperText_rsEnabled, true);

            a.recycle();
        }

        applyStyle();
    }

    // ---------- Public API ----------

    public void setText(@Nullable String t) {
        text.setText(t == null ? "" : t);
    }

    public void setState(int state) {
        this.rsState = state;
        applyStyle();
    }

    public void setRsEnabled(boolean enabled) {
        this.rsEnabled = enabled;
        setEnabled(enabled);
        applyStyle();
    }

    // ---------- Style Logic ----------

    private void applyStyle() {
        if (!rsEnabled) {
            text.setTextColor(colorDisabled);
            icon.setAlpha(0.55f);
            return;
        }

        icon.setAlpha(1f);

        switch (rsState) {
            case STATE_ERROR:
                text.setTextColor(colorError);
                iconBox.setBackgroundTintList(ColorStateList.valueOf(colorError));
                icon.setColorFilter(Color.WHITE);
                break;

            case STATE_SUCCESS:
                text.setTextColor(colorSuccess);
                iconBox.setBackgroundTintList(ColorStateList.valueOf(colorSuccess));
                icon.setColorFilter(Color.WHITE);
                break;

            default:
                text.setTextColor(colorNormal);
                iconBox.setBackgroundTintList(ColorStateList.valueOf(colorNormal));
                icon.setColorFilter(Color.WHITE);
                break;
        }
    }
}
