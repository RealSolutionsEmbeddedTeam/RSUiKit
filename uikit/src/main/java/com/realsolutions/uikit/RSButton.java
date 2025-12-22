package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import java.util.Objects;

/**
 * RealSolutions UI Kit Button
 *
 * Usage (XML):
 *  <com.realsolutions.uikit.RSButton
 *      android:layout_width="match_parent"
 *      android:layout_height="wrap_content"
 *      android:text="Kaydet"
 *      app:rsType="primary" />
 *
 * Requires:
 *  - res/values/attrs.xml with <attr name="rsType" .../>
 *  - drawables & colors:
 *      rs_btn_primary_bg + rs_btn_primary_text
 *      rs_btn_secondary_bg + rs_btn_secondary_text
 *      rs_btn_neutral_bg + rs_btn_neutral_text
 *      rs_btn_plain_dark_bg + rs_btn_plain_dark_text
 *      rs_btn_plain_light_bg + rs_btn_plain_light_text
 */
public class RSButton extends MaterialButton {

    // Must match attrs.xml values
    public static final int TYPE_PRIMARY = 0;
    public static final int TYPE_SECONDARY = 1;
    public static final int TYPE_NEUTRAL = 2;
    public static final int TYPE_PLAIN_DARK = 3;
    public static final int TYPE_PLAIN_LIGHT = 4;

    private int type = TYPE_PRIMARY;

    public RSButton(Context context) {
        super(context);
        init(null);
    }

    public RSButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RSButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        // Defaults
        setAllCaps(false);
        setMinHeight(dp(48));
        setCornerRadius(dp(12));
        setInsetTop(0);
        setInsetBottom(0);

        // Optional: keep padding nice if user doesn't set it
        // (MaterialButton already has padding, but some themes override it)
        if (getPaddingLeft() == 0 && getPaddingRight() == 0) {
            int pxH = dp(16);
            int pxV = dp(12);
            setPadding(pxH, pxV, pxH, pxV);
        }

        // Read rsType from XML
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RSButton);
            type = a.getInt(R.styleable.RSButton_rsType, TYPE_PRIMARY);
            a.recycle();
        }

        applyType(type);
    }

    /**
     * Apply style type programmatically.
     */
    public void applyType(int type) {
        this.type = type;

        switch (type) {
            case TYPE_SECONDARY:
                setBackground(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.rs_btn_secondary_bg)));
                setTextColor(ContextCompat.getColorStateList(getContext(), R.color.rs_btn_secondary_text));
                break;

            case TYPE_NEUTRAL:
                setBackground(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.rs_btn_neutral_bg)));
                setTextColor(ContextCompat.getColorStateList(getContext(), R.color.rs_btn_neutral_text));
                break;

            case TYPE_PLAIN_DARK:
                setBackground(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.rs_btn_plain_dark_bg)));
                setTextColor(ContextCompat.getColorStateList(getContext(), R.color.rs_btn_plain_dark_text));
                break;

            case TYPE_PLAIN_LIGHT:
                setBackground(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.rs_btn_plain_light_bg)));
                setTextColor(ContextCompat.getColorStateList(getContext(), R.color.rs_btn_plain_light_text));
                break;

            case TYPE_PRIMARY:
            default:
                setBackground(Objects.requireNonNull(ContextCompat.getDrawable(getContext(), R.drawable.rs_btn_primary_bg)));
                setTextColor(ContextCompat.getColorStateList(getContext(), R.color.rs_btn_primary_text));
                break;
        }

        // Make sure it redraws
        invalidate();
        requestLayout();
    }

    /**
     * Optional: read current type.
     */
    public int getType() {
        return type;
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
