package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class RSLabel extends LinearLayout {

    private TextView tvText;
    private TextView tvRequired;
    private TextView tvSuffix;
    private ImageView ivInfo;

    private boolean rsRequired = false;
    private boolean rsShowInfo = false;
    private boolean rsEnabled = true;

    private int colorTextEnabled;
    private int colorTextDisabled;
    private int colorSuffixEnabled;
    private int colorSuffixDisabled;
    private int colorRequired; // mavi yıldız
    private int colorInfoEnabled; // gri daire
    private int colorInfoDisabled;

    public RSLabel(Context context) {
        this(context, null);
    }

    public RSLabel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);

        LayoutInflater.from(context).inflate(R.layout.rs_label, this, true);

        tvText = findViewById(R.id.rsLabelText);
        tvRequired = findViewById(R.id.rsLabelRequired);
        tvSuffix = findViewById(R.id.rsLabelSuffix);
        ivInfo = findViewById(R.id.rsLabelInfo);

        colorTextEnabled = ContextCompat.getColor(context, R.color.rs_text_primary);
        colorTextDisabled = ContextCompat.getColor(context, R.color.rs_text_disabled);

        colorSuffixEnabled = ContextCompat.getColor(context, R.color.rs_text_placeholder);
        colorSuffixDisabled = ContextCompat.getColor(context, R.color.rs_text_disabled);

        colorRequired = ContextCompat.getColor(context, R.color.rs_text_brand_solid);

        colorInfoEnabled = ContextCompat.getColor(context, R.color.rs_text_placeholder);
        colorInfoDisabled = ContextCompat.getColor(context, R.color.rs_text_disabled);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RSLabel);

            String text = a.getString(R.styleable.RSLabel_rsText);
            setText(text);

            rsRequired = a.getBoolean(R.styleable.RSLabel_rsRequired, false);
            setRequired(rsRequired);

            String suffix = a.getString(R.styleable.RSLabel_rsSuffixText);
            setSuffixText(suffix);

            rsShowInfo = a.getBoolean(R.styleable.RSLabel_rsShowInfo, false);
            setShowInfo(rsShowInfo);

            rsEnabled = a.getBoolean(R.styleable.RSLabel_rsEnabled, true);
            setRsEnabled(rsEnabled);

            a.recycle();
        } else {
            applyColors();
        }
    }

    // ---- Public API ----

    public void setText(@Nullable String text) {
        tvText.setText(text == null ? "" : text);
    }

    public void setRequired(boolean required) {
        this.rsRequired = required;
        tvRequired.setVisibility(required ? VISIBLE : GONE);
        applyColors();
    }

    public void setSuffixText(@Nullable String suffix) {
        if (suffix == null || suffix.trim().isEmpty()) {
            tvSuffix.setVisibility(GONE);
            tvSuffix.setText("");
        } else {
            tvSuffix.setVisibility(VISIBLE);
            tvSuffix.setText(suffix);
        }
        applyColors();
    }

    public void setShowInfo(boolean show) {
        this.rsShowInfo = show;
        ivInfo.setVisibility(show ? VISIBLE : GONE);
        applyColors();
    }

    public void setOnInfoClickListener(@Nullable OnClickListener l) {
        ivInfo.setOnClickListener(l);
        ivInfo.setClickable(l != null);
        ivInfo.setFocusable(l != null);
    }

    public void setRsEnabled(boolean enabled) {
        this.rsEnabled = enabled;
        setEnabled(enabled);
        applyColors();
    }

    private void applyColors() {
        boolean enabled = rsEnabled;

        tvText.setTextColor(enabled ? colorTextEnabled : colorTextDisabled);
        tvSuffix.setTextColor(enabled ? colorSuffixEnabled : colorSuffixDisabled);
        tvRequired.setTextColor(colorRequired);

        // Don't apply tint to info icon - it's a multi-color vector (grey circle +
        // white 'i')
        // Tint would override all colors and make it invisible
        if (ivInfo.getVisibility() == VISIBLE) {
            ivInfo.setAlpha(enabled ? 1.0f : 0.5f);
        }
    }
}
