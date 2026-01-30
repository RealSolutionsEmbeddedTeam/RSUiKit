package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

public class RSInput extends LinearLayout {

    public static final int SIZE_XS = 0;
    public static final int SIZE_SM = 1;
    public static final int SIZE_MD = 2;

    public static final int STATE_NORMAL = 0;
    public static final int STATE_FOCUSED = 1;
    public static final int STATE_ERROR = 2;
    public static final int STATE_SUCCESS = 3;
    public static final int STATE_HOVERED = 4;

    public static final int TYPE_BASIC = 0;
    public static final int TYPE_EMAIL = 1;
    public static final int TYPE_PASSWORD = 2;
    public static final int TYPE_SEARCH = 3;
    public static final int TYPE_WEBSITE = 4;
    public static final int TYPE_DATE = 5;
    public static final int TYPE_LINK = 6;
    public static final int TYPE_DROPDOWN = 7;
    public static final int TYPE_PHONE = 8;
    public static final int TYPE_BUTTON = 9;

    private RSLabel rsLabel;
    private LinearLayout container;
    private LinearLayout prefixBox;
    private TextView prefixText;
    private View prefixDivider;
    private ImageView leadingIcon;
    private TextInputEditText et;
    private ImageView infoIcon;
    private ImageView trailingIcon;
    private ImageView copyIcon;
    private LinearLayout copyBox;
    private View copyDivider;
    private View suffixDivider;
    private LinearLayout suffixBox;
    private ImageView suffixIcon;
    private TextView suffixText;
    private ImageView suffixChevron;
    private LinearLayout helperLayout;
    private ImageView helperIcon;
    private TextView helperText;

    private boolean showInfo = false;
    private boolean hasCustomLeadingIcon = false;

    private int state = STATE_NORMAL;
    private int size = SIZE_MD;
    private int type = TYPE_BASIC;
    private boolean isRsEnabled = true;
    private boolean isPasswordVisible = false;

    public RSInput(Context context) {
        this(context, null);
    }

    public RSInput(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.rs_input, this, true);

        rsLabel = findViewById(R.id.rsInputLabel);
        container = findViewById(R.id.rsInputContainer);
        prefixBox = findViewById(R.id.rsInputPrefixBox);
        prefixText = findViewById(R.id.rsInputPrefixText);
        prefixDivider = findViewById(R.id.rsInputPrefixDivider);
        leadingIcon = findViewById(R.id.rsInputLeadingIcon);
        et = findViewById(R.id.rsInputEt);
        infoIcon = findViewById(R.id.rsInputInfoIcon);
        trailingIcon = findViewById(R.id.rsInputTrailingIcon);
        copyIcon = findViewById(R.id.rsInputCopyIcon);
        copyBox = findViewById(R.id.rsInputCopyBox);
        copyDivider = findViewById(R.id.rsInputCopyDivider);
        suffixDivider = findViewById(R.id.rsInputSuffixDivider);
        suffixBox = findViewById(R.id.rsInputSuffixBox);
        suffixIcon = findViewById(R.id.rsInputSuffixIcon);
        suffixText = findViewById(R.id.rsInputSuffixText);
        suffixChevron = findViewById(R.id.rsInputSuffixChevron);
        helperLayout = findViewById(R.id.rsInputHelperLayout);
        helperIcon = findViewById(R.id.rsInputHelperIcon);
        helperText = findViewById(R.id.rsInputHelperText);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RSInput);

            String label = a.getString(R.styleable.RSInput_rsLabel);
            rsLabel.setText(label);
            rsLabel.setVisibility(label != null ? VISIBLE : GONE);

            rsLabel.setRequired(a.getBoolean(R.styleable.RSInput_rsRequired, false));
            showInfo = a.getBoolean(R.styleable.RSInput_rsShowInfo, false);
            rsLabel.setShowInfo(showInfo);
            rsLabel.setSuffixText(a.getString(R.styleable.RSInput_rsSuffixText));

            et.setHint(a.getString(R.styleable.RSInput_rsHint));
            setText(a.getString(R.styleable.RSInput_rsText));
            setHelperText(a.getString(R.styleable.RSInput_rsHelperText));

            state = a.getInt(R.styleable.RSInput_rsState, STATE_NORMAL);
            size = a.getInt(R.styleable.RSInput_rsSize, SIZE_MD);
            type = a.getInt(R.styleable.RSInput_rsInputType, TYPE_BASIC);
            isRsEnabled = a.getBoolean(R.styleable.RSInput_rsEnabled, true);

            setPrefixText(a.getString(R.styleable.RSInput_rsPrefixText));

            Drawable leading = a.getDrawable(R.styleable.RSInput_rsLeadingIcon);
            if (leading != null) {
                leadingIcon.setImageDrawable(leading);
                leadingIcon.setVisibility(VISIBLE);
                hasCustomLeadingIcon = true;
            }

            Drawable trailing = a.getDrawable(R.styleable.RSInput_rsTrailingIcon);
            if (trailing != null) {
                trailingIcon.setImageDrawable(trailing);
                trailingIcon.setVisibility(VISIBLE);
            }

            a.recycle();
        }

        setupInteraction();
        applyAll();
    }

    private void setupInteraction() {
        et.setOnFocusChangeListener((v, hasFocus) -> {
            if (state != STATE_ERROR && state != STATE_SUCCESS && isRsEnabled) {
                setState(hasFocus ? STATE_FOCUSED : STATE_NORMAL);
            }
        });

        // Click on container should focus EditText
        container.setOnClickListener(v -> {
            if (isRsEnabled) {
                et.requestFocus();
            }
        });

        et.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                updateFilledState();
            }
        });

        et.setOnHoverListener((v, event) -> {
            if (state != STATE_ERROR && state != STATE_SUCCESS && state != STATE_FOCUSED && isRsEnabled) {
                if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                    applyStateStyles(STATE_HOVERED);
                } else if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                    applyStateStyles(STATE_NORMAL);
                }
            }
            return false;
        });

        trailingIcon.setOnClickListener(v -> {
            if (type == TYPE_PASSWORD) {
                togglePasswordVisibility();
            }
        });
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            trailingIcon.setImageResource(R.drawable.rs_ic_eye);
        } else {
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            trailingIcon.setImageResource(R.drawable.rs_ic_eye);
        }
        et.setSelection(et.getText().length());
    }

    private void applyAll() {
        applySize(size);
        applyType(type);
        applyState(state);
        setRsEnabled(isRsEnabled);
    }

    public void setState(int state) {
        this.state = state;
        applyState(state);
    }

    public void setSize(int size) {
        this.size = size;
        applySize(size);
    }

    public void setType(int type) {
        this.type = type;
        applyType(type);
    }

    public void setPrefixText(String text) {
        if (text != null && !text.isEmpty()) {
            prefixText.setText(text);
            prefixBox.setVisibility(VISIBLE);
            prefixDivider.setVisibility(VISIBLE);
        } else {
            prefixBox.setVisibility(GONE);
            prefixDivider.setVisibility(GONE);
        }
    }

    public void setRsEnabled(boolean enabled) {
        this.isRsEnabled = enabled;
        et.setEnabled(enabled);
        container.setEnabled(enabled);
        rsLabel.setRsEnabled(enabled);
        updateHelperState();
        applyState(state);
    }

    private void updateFilledState() {
        // We don't change the 'state' variable directly if it's Error or Focus,
        // but it might affect visual cues if we had a 'Filled' specific style.
        // For now, normal and filled use similar basics, but it's good to have.
        applyStateStyles(state);
    }

    public void setHelperText(String text) {
        if (text == null || text.isEmpty()) {
            helperLayout.setVisibility(GONE);
        } else {
            helperLayout.setVisibility(VISIBLE);
            helperText.setText(text);
        }
    }

    private void applyState(int st) {
        applyStateStyles(st);
        updateHelperState();
    }

    private void applyStateStyles(int st) {
        int strokeColorRes;
        int bgColorRes;
        int iconTintRes;
        int prefixBgRes = R.color.rs_bg_secondary;

        if (!isRsEnabled) {
            strokeColorRes = R.color.rs_border_disabled;
            bgColorRes = R.color.rs_bg_disabled_subtle;
            iconTintRes = R.color.rs_text_disabled;
            prefixBgRes = R.color.rs_bg_disabled;
        } else {
            switch (st) {
                case STATE_FOCUSED:
                    strokeColorRes = R.color.rs_border_brand_solid;
                    bgColorRes = R.color.rs_bg_primary;
                    iconTintRes = R.color.rs_text_tertiary; // Icons stay tertiary in focus usually
                    break;
                case STATE_ERROR:
                    strokeColorRes = R.color.rs_border_error_solid;
                    bgColorRes = R.color.rs_bg_primary;
                    iconTintRes = R.color.rs_text_error_solid;
                    break;
                case STATE_SUCCESS:
                    strokeColorRes = R.color.rs_border_success_solid;
                    bgColorRes = R.color.rs_bg_primary;
                    iconTintRes = R.color.rs_text_success_solid;
                    break;
                case STATE_HOVERED:
                    strokeColorRes = R.color.rs_border_strong;
                    bgColorRes = R.color.rs_bg_primary_on_hover;
                    iconTintRes = R.color.rs_text_tertiary;
                    break;
                case STATE_NORMAL:
                default:
                    strokeColorRes = R.color.rs_border_primary;
                    bgColorRes = R.color.rs_bg_primary;
                    iconTintRes = R.color.rs_text_tertiary;
                    break;
            }
        }

        // Apply manually to the container background
        int strokeColor = ContextCompat.getColor(getContext(), strokeColorRes);
        int bgColor = ContextCompat.getColor(getContext(), bgColorRes);
        int iconColor = ContextCompat.getColor(getContext(), iconTintRes);

        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(getResources().getDimension(R.dimen.rs_input_radius_new));
        bg.setStroke(1, strokeColor);
        bg.setColor(bgColor);
        container.setBackground(bg);

        // Helper styling
        if (st == STATE_ERROR) {
            helperText.setTextColor(ContextCompat.getColor(getContext(), R.color.rs_text_error_solid));
            helperIcon.setImageResource(R.drawable.rs_ic_error);
            helperIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.rs_text_error_solid));
            helperIcon.setVisibility(VISIBLE);
        } else {
            helperText.setTextColor(ContextCompat.getColor(getContext(), R.color.rs_text_secondary));
            helperIcon.setImageResource(R.drawable.rs_ic_info_circle);
            helperIcon.setColorFilter(iconColor);
        }

        ColorStateList tint = ColorStateList.valueOf(iconColor);
        leadingIcon.setImageTintList(tint);
        trailingIcon.setImageTintList(tint);

        prefixBox.setBackgroundColor(ContextCompat.getColor(getContext(), prefixBgRes));
        prefixDivider.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.rs_border_primary));
    }

    private void applySize(int sz) {
        int heightRes;
        switch (sz) {
            case SIZE_XS:
                heightRes = R.dimen.rs_input_height_xs;
                break;
            case SIZE_SM:
                heightRes = R.dimen.rs_input_height_sm;
                break;
            case SIZE_MD:
            default:
                heightRes = R.dimen.rs_input_height_md;
                break;
        }
        ViewGroup.LayoutParams params = container.getLayoutParams();
        params.height = getResources().getDimensionPixelSize(heightRes);
        container.setLayoutParams(params);
    }

    private void applyType(int tp) {
        this.type = tp;
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        // Don't hide leading icon if custom icon was set via XML attribute
        if (!hasCustomLeadingIcon) {
            leadingIcon.setVisibility(GONE);
        }
        trailingIcon.setVisibility(GONE);
        copyBox.setVisibility(GONE);
        copyDivider.setVisibility(GONE);
        suffixDivider.setVisibility(GONE);
        suffixBox.setVisibility(GONE);
        setPrefixText(null);

        // Show info icon if rsShowInfo is true (except for password and dropdown)
        boolean showInfoIcon = showInfo && (tp != TYPE_PASSWORD) && (tp != TYPE_DROPDOWN);
        infoIcon.setVisibility(showInfoIcon ? VISIBLE : GONE);

        switch (tp) {
            case TYPE_BASIC:
                // Basic type - uses custom leading icon if set via XML, no type-specific icons
                // hasCustomLeadingIcon already handled above
                break;
            case TYPE_EMAIL:
                et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                leadingIcon.setImageResource(R.drawable.rs_ic_mail);
                leadingIcon.setVisibility(VISIBLE);
                break;
            case TYPE_PASSWORD:
                et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                leadingIcon.setImageResource(R.drawable.rs_ic_lock);
                leadingIcon.setVisibility(VISIBLE);
                trailingIcon.setImageResource(R.drawable.rs_ic_eye);
                trailingIcon.setVisibility(VISIBLE);
                // No info icon for password - only eye icon
                break;
            case TYPE_SEARCH:
                leadingIcon.setImageResource(R.drawable.rs_ic_search);
                leadingIcon.setVisibility(VISIBLE);
                break;
            case TYPE_WEBSITE:
                setPrefixText("https://");
                break;
            case TYPE_LINK:
                leadingIcon.setImageResource(R.drawable.rs_ic_link);
                leadingIcon.setVisibility(VISIBLE);
                copyDivider.setVisibility(VISIBLE);
                copyBox.setVisibility(VISIBLE);
                break;
            case TYPE_DROPDOWN:
                // Show user icon for dropdown (Invite Members)
                leadingIcon.setImageResource(R.drawable.rs_ic_user);
                leadingIcon.setVisibility(VISIBLE);
                // Show suffix box with globe icon (inside border, no divider)
                suffixBox.setVisibility(VISIBLE);
                break;
            case TYPE_DATE:
                leadingIcon.setImageResource(R.drawable.rs_ic_calendar);
                leadingIcon.setVisibility(VISIBLE);
                break;
            case TYPE_BUTTON:
                // Button type - clickable input with trailing chevron
                et.setFocusable(false);
                et.setClickable(true);
                trailingIcon.setImageResource(R.drawable.rs_ic_chevron_down);
                trailingIcon.setVisibility(VISIBLE);
                break;
        }
    }

    private void updateHelperState() {
        float alpha = isRsEnabled ? 1.0f : 0.5f;
        helperIcon.setAlpha(alpha);
        helperText.setAlpha(alpha);
    }

    public String getText() {
        return et.getText() != null ? et.getText().toString() : "";
    }

    public void setText(String text) {
        et.setText(text);
    }

    /**
     * Set a click listener for the copy icon (visible in Link input type)
     */
    public void setOnCopyClickListener(@Nullable OnClickListener listener) {
        copyIcon.setOnClickListener(listener);
    }

    /**
     * Set a click listener for the suffix box (visible in Dropdown input type)
     */
    public void setOnSuffixClickListener(@Nullable OnClickListener listener) {
        suffixBox.setOnClickListener(listener);
    }

    /**
     * Set a click listener for the info icon
     */
    public void setOnInfoClickListener(@Nullable OnClickListener listener) {
        infoIcon.setOnClickListener(listener);
    }

    /**
     * Set the suffix text in dropdown (e.g., "can view", "can edit")
     */
    public void setSuffixText(String text) {
        if (suffixText != null) {
            suffixText.setText(text);
        }
    }

    /**
     * Set whether to show the info icon in the input field
     */
    public void setShowInfo(boolean show) {
        this.showInfo = show;
        rsLabel.setShowInfo(show);
        // Only show infoIcon if not dropdown type (dropdown uses suffix box instead)
        if (type != TYPE_DROPDOWN) {
            infoIcon.setVisibility(show ? VISIBLE : GONE);
        }
    }
}
