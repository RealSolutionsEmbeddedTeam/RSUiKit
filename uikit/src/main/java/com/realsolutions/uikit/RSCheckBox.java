package com.realsolutions.uikit;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.Objects;

public class RSCheckBox extends MaterialCheckBox {

    public RSCheckBox(Context context) {
        super(context);
        init(null);
    }

    public RSCheckBox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RSCheckBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {

        // Default
        setChecked(false);
        setMinHeight(16);
        setMinWidth(20);
        setActivated(true);

        // Set Background
        setBackground(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.rs_btn_primary_bg)));
        setTextColor(ContextCompat.getColorStateList(getContext(), R.color.rs_btn_primary_text));

        // Make sure it redraws
        invalidate();
        requestLayout();
    }
}
