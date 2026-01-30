package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * RSCounterInput - Counter input component with +/- buttons
 * <p>
 * Features:
 * - Three sizes: XS (32dp), SM (36dp), MD (40dp)
 * - States: Normal, Error, Disabled
 * - Label with required indicator and sublabel
 * - Optional info icon
 * - Hint text below counter
 * - Min/max value limits
 * - Step value for increment/decrement
 */
public class RSCounterInput extends LinearLayout {

    // Size constants (matches rsSize enum values)
    public static final int SIZE_XS = 0; // 32dp
    public static final int SIZE_SM = 1; // 36dp
    public static final int SIZE_MD = 2; // 40dp

    // State constants (matches Figma states)
    public static final int STATE_DEFAULT = 0;
    public static final int STATE_HOVER = 1;
    public static final int STATE_FOCUS = 2;
    public static final int STATE_FILLED = 3;
    public static final int STATE_DISABLED = 4;
    public static final int STATE_ERROR = 5;

    // Views
    private LinearLayout labelRow;
    private TextView tvLabel;
    private TextView tvRequired;
    private TextView tvSublabel;
    private ImageView ivLabelInfo;

    private LinearLayout inputRow;
    private ImageView btnMinus;
    private TextView tvValue;
    private ImageView btnPlus;

    private LinearLayout hintRow;
    private ImageView ivHintIcon;
    private TextView tvHint;

    // State
    private int currentSize = SIZE_MD;
    private int currentState = STATE_DEFAULT;
    private boolean rsEnabled = true;
    private boolean rsRequired = false;
    private boolean rsShowInfo = false;
    private boolean isFocused = false;
    private boolean isHovered = false;

    // Counter state
    private int value = 0;
    private int minValue = Integer.MIN_VALUE;
    private int maxValue = Integer.MAX_VALUE;
    private int step = 1;

    // Colors
    private int colorTextPrimary;
    private int colorTextTertiary;
    private int colorTextDisabled;
    private int colorTextBrand;
    private int colorIconDisabled;
    private int colorTextError;
    private int colorIconError;

    // Listener
    private OnValueChangeListener onValueChangeListener;

    public interface OnValueChangeListener {
        void onValueChanged(int newValue);
    }

    public RSCounterInput(Context context) {
        this(context, null);
    }

    public RSCounterInput(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RSCounterInput(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.rs_counter_input, this, true);

        // Find views
        labelRow = findViewById(R.id.rsCounterLabelRow);
        tvLabel = findViewById(R.id.rsCounterLabel);
        tvRequired = findViewById(R.id.rsCounterRequired);
        tvSublabel = findViewById(R.id.rsCounterSublabel);
        ivLabelInfo = findViewById(R.id.rsCounterInfoIcon);

        inputRow = findViewById(R.id.rsCounterInputRow);
        btnMinus = findViewById(R.id.rsCounterBtnMinus);
        tvValue = findViewById(R.id.rsCounterValue);
        btnPlus = findViewById(R.id.rsCounterBtnPlus);

        hintRow = findViewById(R.id.rsCounterHintRow);
        ivHintIcon = findViewById(R.id.rsCounterHintIcon);
        tvHint = findViewById(R.id.rsCounterHint);

        // Init colors
        colorTextPrimary = ContextCompat.getColor(context, R.color.rs_text_primary);
        colorTextTertiary = ContextCompat.getColor(context, R.color.rs_text_tertiary);
        colorTextDisabled = ContextCompat.getColor(context, R.color.rs_text_disabled);
        colorTextBrand = ContextCompat.getColor(context, R.color.rs_text_brand_tertiary);
        colorIconDisabled = ContextCompat.getColor(context, R.color.rs_text_disabled);
        colorTextError = ContextCompat.getColor(context, R.color.rs_text_error_tertiary);
        colorIconError = ContextCompat.getColor(context, R.color.rs_text_error_tertiary);

        // Setup click listeners
        btnMinus.setOnClickListener(v -> decrement());
        btnPlus.setOnClickListener(v -> increment());

        // Setup focus handling
        inputRow.setFocusable(true);
        inputRow.setFocusableInTouchMode(true);
        inputRow.setOnFocusChangeListener((v, hasFocus) -> {
            this.isFocused = hasFocus;
            updateVisualState();
        });

        // Setup touch/hover handling
        inputRow.setOnTouchListener((v, event) -> {
            if (!rsEnabled)
                return false;

            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    isHovered = true;
                    updateVisualState();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                case android.view.MotionEvent.ACTION_CANCEL:
                    isHovered = false;
                    updateVisualState();
                    break;
            }
            return false;
        });

        // Parse attributes
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RSCounterInput);

            // Size
            currentSize = a.getInt(R.styleable.RSCounterInput_rsSize, SIZE_MD);

            // State
            int stateAttr = a.getInt(R.styleable.RSCounterInput_rsState, 0);
            if (stateAttr == 1) { // focused
                isFocused = true;
            } else if (stateAttr == 2) { // error
                currentState = STATE_ERROR;
            } else if (stateAttr == 4) { // hovered
                isHovered = true;
            }

            // Label
            String label = a.getString(R.styleable.RSCounterInput_rsLabel);
            setLabel(label);

            // Required
            rsRequired = a.getBoolean(R.styleable.RSCounterInput_rsRequired, false);
            setRequired(rsRequired);

            // Sublabel
            String sublabel = a.getString(R.styleable.RSCounterInput_rsCounterSublabel);
            setSublabel(sublabel);

            // Show info
            rsShowInfo = a.getBoolean(R.styleable.RSCounterInput_rsShowInfo, false);
            setShowInfo(rsShowInfo);

            // Hint
            String hint = a.getString(R.styleable.RSCounterInput_rsCounterHint);
            setHint(hint);

            // Counter values
            value = a.getInt(R.styleable.RSCounterInput_rsCounterValue, 0);
            minValue = a.getInt(R.styleable.RSCounterInput_rsCounterMinValue, Integer.MIN_VALUE);
            maxValue = a.getInt(R.styleable.RSCounterInput_rsCounterMaxValue, Integer.MAX_VALUE);
            step = a.getInt(R.styleable.RSCounterInput_rsCounterStep, 1);

            // Enabled
            rsEnabled = a.getBoolean(R.styleable.RSCounterInput_rsEnabled, true);

            a.recycle();
        }

        // Apply initial state
        applySize();
        applyState();
        updateValueDisplay();
        updateButtonStates();
    }

    // ==================== Public API ====================

    public void setLabel(@Nullable String label) {
        if (label == null || label.trim().isEmpty()) {
            labelRow.setVisibility(GONE);
        } else {
            labelRow.setVisibility(VISIBLE);
            tvLabel.setText(label);
        }
    }

    public void setRequired(boolean required) {
        this.rsRequired = required;
        tvRequired.setVisibility(required ? VISIBLE : GONE);
    }

    public void setSublabel(@Nullable String sublabel) {
        if (sublabel == null || sublabel.trim().isEmpty()) {
            tvSublabel.setVisibility(GONE);
        } else {
            tvSublabel.setVisibility(VISIBLE);
            tvSublabel.setText(sublabel);
        }
    }

    public void setShowInfo(boolean show) {
        this.rsShowInfo = show;
        ivLabelInfo.setVisibility(show ? VISIBLE : GONE);
    }

    public void setOnInfoClickListener(@Nullable OnClickListener listener) {
        ivLabelInfo.setOnClickListener(listener);
    }

    public void setHint(@Nullable String hint) {
        if (hint == null || hint.trim().isEmpty()) {
            hintRow.setVisibility(GONE);
        } else {
            hintRow.setVisibility(VISIBLE);
            tvHint.setText(hint);
        }
    }

    public void setValue(int newValue) {
        this.value = Math.max(minValue, Math.min(maxValue, newValue));
        updateValueDisplay();
        updateButtonStates();
    }

    public int getValue() {
        return value;
    }

    public void setMinValue(int min) {
        this.minValue = min;
        if (value < min) {
            setValue(min);
        }
        updateButtonStates();
    }

    public void setMaxValue(int max) {
        this.maxValue = max;
        if (value > max) {
            setValue(max);
        }
        updateButtonStates();
    }

    public void setStep(int step) {
        this.step = Math.max(1, step);
    }

    public void setSize(int size) {
        this.currentSize = size;
        applySize();
    }

    public void setState(int state) {
        this.currentState = state;
        applyState();
    }

    public void setRsEnabled(boolean enabled) {
        this.rsEnabled = enabled;
        applyState();
    }

    public void setOnValueChangeListener(@Nullable OnValueChangeListener listener) {
        this.onValueChangeListener = listener;
    }

    // ==================== Internal Methods ====================

    private void increment() {
        if (!rsEnabled)
            return;
        int newValue = value + step;
        if (newValue <= maxValue) {
            value = newValue;
            updateValueDisplay();
            updateButtonStates();
            notifyValueChanged();
        }
    }

    private void decrement() {
        if (!rsEnabled)
            return;
        int newValue = value - step;
        if (newValue >= minValue) {
            value = newValue;
            updateValueDisplay();
            updateButtonStates();
            notifyValueChanged();
        }
    }

    private void notifyValueChanged() {
        if (onValueChangeListener != null) {
            onValueChangeListener.onValueChanged(value);
        }
    }

    private void updateValueDisplay() {
        tvValue.setText(String.valueOf(value));
    }

    private void updateButtonStates() {
        boolean canDecrement = rsEnabled && value > minValue;
        boolean canIncrement = rsEnabled && value < maxValue;

        btnMinus.setAlpha(canDecrement ? 1.0f : 0.4f);
        btnMinus.setClickable(canDecrement);

        btnPlus.setAlpha(canIncrement ? 1.0f : 0.4f);
        btnPlus.setClickable(canIncrement);
    }

    private void applySize() {
        int heightDp;
        switch (currentSize) {
            case SIZE_XS:
                heightDp = 32;
                break;
            case SIZE_SM:
                heightDp = 36;
                break;
            case SIZE_MD:
            default:
                heightDp = 40;
                break;
        }

        int heightPx = dpToPx(heightDp);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) inputRow.getLayoutParams();
        params.height = heightPx;
        inputRow.setLayoutParams(params);
    }

    private void updateVisualState() {
        // Determine current visual state based on priority
        int visualState;

        if (!rsEnabled) {
            visualState = STATE_DISABLED;
        } else if (currentState == STATE_ERROR) {
            visualState = STATE_ERROR;
        } else if (isFocused) {
            visualState = STATE_FOCUS;
        } else if (isHovered) {
            visualState = STATE_HOVER;
        } else if (value != 0) {
            visualState = STATE_FILLED;
        } else {
            visualState = STATE_DEFAULT;
        }

        // Apply background based on visual state
        switch (visualState) {
            case STATE_DISABLED:
                inputRow.setBackgroundResource(R.drawable.rs_counter_input_bg_disabled);
                break;
            case STATE_ERROR:
                inputRow.setBackgroundResource(R.drawable.rs_counter_input_bg_error);
                break;
            case STATE_FOCUS:
                inputRow.setBackgroundResource(R.drawable.rs_counter_input_bg_focused);
                break;
            case STATE_HOVER:
                inputRow.setBackgroundResource(R.drawable.rs_counter_input_bg_hover);
                break;
            case STATE_FILLED:
                inputRow.setBackgroundResource(R.drawable.rs_counter_input_bg_filled);
                break;
            case STATE_DEFAULT:
            default:
                inputRow.setBackgroundResource(R.drawable.rs_counter_input_bg_default);
                break;
        }

        // Apply text and icon colors based on enabled/error state
        if (!rsEnabled) {
            tvValue.setTextColor(colorTextDisabled);
            tvLabel.setTextColor(colorTextDisabled);
            tvSublabel.setTextColor(colorTextDisabled);
            tvHint.setTextColor(colorTextDisabled);
            ivLabelInfo.setAlpha(0.5f);
            ivHintIcon.setAlpha(0.5f);
            btnMinus.setColorFilter(colorIconDisabled);
            btnPlus.setColorFilter(colorIconDisabled);
        } else if (currentState == STATE_ERROR) {
            tvValue.setTextColor(colorTextPrimary);
            tvLabel.setTextColor(colorTextPrimary);
            tvSublabel.setTextColor(colorTextTertiary);
            tvHint.setTextColor(colorTextTertiary); // Keep normal as requested
            ivLabelInfo.setAlpha(1.0f);
            ivLabelInfo.clearColorFilter();
            ivHintIcon.setAlpha(1.0f);
            ivHintIcon.clearColorFilter(); // Keep normal as requested
            btnMinus.clearColorFilter();
            btnPlus.clearColorFilter();
        } else {
            tvValue.setTextColor(colorTextPrimary);
            tvLabel.setTextColor(colorTextPrimary);
            tvSublabel.setTextColor(colorTextTertiary);
            tvHint.setTextColor(colorTextTertiary);
            ivLabelInfo.setAlpha(1.0f);
            ivLabelInfo.clearColorFilter();
            ivHintIcon.setAlpha(1.0f);
            ivHintIcon.clearColorFilter();
            btnMinus.clearColorFilter();
            btnPlus.clearColorFilter();
        }

        updateButtonStates();
    }

    // Keep applyState for backward compatibility
    private void applyState() {
        updateVisualState();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
