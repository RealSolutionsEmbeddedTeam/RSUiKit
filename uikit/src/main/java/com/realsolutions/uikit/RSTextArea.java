package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.realsolutions.uikit.R;

public class RSTextArea extends FrameLayout {

    // RSState (mevcut enum değerleri)
    public static final int STATE_NORMAL  = 0;
    public static final int STATE_FOCUSED = 1;
    public static final int STATE_ERROR   = 2;
    public static final int STATE_SUCCESS = 3;

    private FrameLayout root;
    private EditText input;
    private TextView counter;

    private int rsState = STATE_NORMAL;
    private boolean rsEnabled = true;

    private int maxLength = 200;
    private boolean showCounter = true;

    // tokens (palette isimlerini projene göre eşle)
    private int cBg;
    private int cBgDisabled;

    private int cStrokeNormal;
    private int cStrokeFocused;
    private int cStrokeError;
    private int cStrokeSuccess;
    private int cStrokeDisabled;

    private int cText;
    private int cHint;
    private int cDisabledText;

    private int cCounter;

    public RSTextArea(@NonNull Context context) {
        this(context, null);
    }

    public RSTextArea(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.rs_text_area, this, true);

        root = findViewById(R.id.rsTaRoot);
        input = findViewById(R.id.rsTaInput);
        counter = findViewById(R.id.rsTaCounter);

        // tokens
        cBg = ContextCompat.getColor(context, R.color.rs_base_white);
        cBgDisabled = ContextCompat.getColor(context, R.color.rs_bg_disabled);

        cStrokeNormal = ContextCompat.getColor(context, R.color.rs_text_secondary);
        cStrokeFocused = ContextCompat.getColor(context, R.color.rs_text_brand_solid);
        cStrokeError = ContextCompat.getColor(context, R.color.rs_text_error_solid);
        cStrokeSuccess = ContextCompat.getColor(context, R.color.rs_text_success_solid);
        cStrokeDisabled = ContextCompat.getColor(context, R.color.rs_border_tertiary);

        cText = ContextCompat.getColor(context, R.color.rs_text_primary);
        cHint = ContextCompat.getColor(context, R.color.rs_text_tertiary);
        cDisabledText = ContextCompat.getColor(context, R.color.rs_text_tertiary);

        cCounter = ContextCompat.getColor(context, R.color.rs_text_tertiary);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RSTextArea);

            String hint = a.getString(R.styleable.RSTextArea_rsHint);
            if (hint != null) input.setHint(hint);

            String text = a.getString(R.styleable.RSTextArea_rsText);
            if (text != null) input.setText(text);

            maxLength = a.getInt(R.styleable.RSTextArea_rsMaxLength, 200);
            showCounter = a.getBoolean(R.styleable.RSTextArea_rsShowCounter, true);

            rsState = a.getInt(R.styleable.RSTextArea_rsState, STATE_NORMAL);
            rsEnabled = a.getBoolean(R.styleable.RSTextArea_rsEnabled, true);

            a.recycle();
        }

        // max length filter
        applyMaxLength(maxLength);

        // counter visibility
        counter.setVisibility(showCounter ? VISIBLE : GONE);

        // counter update
        updateCounter();

        input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCounter();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // focus -> state
        input.setOnFocusChangeListener((v, hasFocus) -> {
            if (!rsEnabled) return;

            if (hasFocus) {
                // sadece normalden focused’a geçelim (error/success bozulmasın)
                if (rsState == STATE_NORMAL) {
                    rsState = STATE_FOCUSED;
                    applyStyle();
                }
            } else {
                if (rsState == STATE_FOCUSED) {
                    rsState = STATE_NORMAL;
                    applyStyle();
                }
            }
        });

        setRsEnabled(rsEnabled);
        applyStyle();
    }

    // --------- Public API ---------

    public EditText getEditText() {
        return input;
    }

    public void setText(@Nullable String t) {
        input.setText(t == null ? "" : t);
        updateCounter();
    }

    public String getText() {
        return input.getText() == null ? "" : input.getText().toString();
    }

    public void setHint(@Nullable String hint) {
        input.setHint(hint);
    }

    public void setMaxLength(int max) {
        maxLength = Math.max(0, max);
        applyMaxLength(maxLength);
        updateCounter();
    }

    public void setShowCounter(boolean show) {
        showCounter = show;
        counter.setVisibility(show ? VISIBLE : GONE);
    }

    public void setState(int state) {
        rsState = state;
        applyStyle();
    }

    public void setRsEnabled(boolean enabled) {
        rsEnabled = enabled;
        input.setEnabled(enabled);
        input.setFocusable(enabled);
        input.setFocusableInTouchMode(enabled);
        applyStyle();
        updateCounter();
    }

    // --------- Internal ---------

    private void applyMaxLength(int max) {
        if (max <= 0) {
            input.setFilters(new InputFilter[]{}); // limit yok
        } else {
            input.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(max) });
        }
    }

    private void updateCounter() {
        if (!showCounter) return;

        int len = input.getText() == null ? 0 : input.getText().length();
        String max = (maxLength > 0) ? String.valueOf(maxLength) : "∞";
        counter.setText(len + "/" + max);

        counter.setTextColor(rsEnabled ? cCounter : cDisabledText);
        counter.setAlpha(rsEnabled ? 1f : 0.55f);
    }

    private void applyStyle() {
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(10));

        int stroke;
        int fill;

        if (!rsEnabled) {
            stroke = cStrokeDisabled;
            fill = cBgDisabled;
            input.setTextColor(cDisabledText);
            input.setHintTextColor(cDisabledText);
            input.setAlpha(0.55f);
        } else {
            fill = cBg;

            switch (rsState) {
                case STATE_ERROR: stroke = cStrokeError; break;
                case STATE_SUCCESS: stroke = cStrokeSuccess; break;
                case STATE_FOCUSED: stroke = cStrokeFocused; break;
                case STATE_NORMAL:
                default: stroke = cStrokeNormal; break;
            }

            input.setAlpha(1f);
            input.setTextColor(cText);
            input.setHintTextColor(cHint);
        }

        bg.setColor(fill);
        bg.setStroke(dp(1), stroke);
        root.setBackground(bg);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
