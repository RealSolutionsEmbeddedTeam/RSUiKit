package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * RSBadge - Yeni Figma tasarımına uygun Badge bileşeni.
 * <p>
 * Figma Specs:
 * - Type: basic, left_icon, right_icon, with_dot
 * - Style: light, lighter, stroke
 * - Color: default, brand, warning, error, success
 * <p>
 * Usage:
 * <com.realsolutions.uikit.RSBadge
 * app:rsBadgeType="left_icon"
 * app:rsBadgeStyle="lighter"
 * app:rsBadgeColor="success"
 * app:rsBadgeText="Badge"
 * app:rsBadgeIcon="@drawable/rs_ic_check" />
 */
public class RSBadge extends LinearLayout {

    // =====================
    // Constants - Type
    // =====================

    public static final int TYPE_BASIC = 0;
    public static final int TYPE_LEFT_ICON = 1;
    public static final int TYPE_RIGHT_ICON = 2;
    public static final int TYPE_WITH_DOT = 3;

    // =====================
    // Constants - Style
    // =====================

    public static final int STYLE_LIGHT = 0;
    public static final int STYLE_LIGHTER = 1;
    public static final int STYLE_STROKE = 2;

    // =====================
    // Constants - Color
    // =====================

    public static final int COLOR_DEFAULT = 0;
    public static final int COLOR_BRAND = 1;
    public static final int COLOR_WARNING = 2;
    public static final int COLOR_ERROR = 3;
    public static final int COLOR_SUCCESS = 4;

    // =====================
    // Views
    // =====================

    private LinearLayout rootLayout;
    private View dotView;
    private ImageView leftIconView;
    private TextView textView;
    private ImageView rightIconView;

    // =====================
    // State
    // =====================

    private int currentType = TYPE_BASIC;
    private int currentStyle = STYLE_LIGHTER;
    private int currentColor = COLOR_DEFAULT;
    private int number = -1;
    private int iconRes = 0;

    // =====================
    // Constructors
    // =====================

    public RSBadge(Context context) {
        super(context);
        init(context, null);
    }

    public RSBadge(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RSBadge(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // =====================
    // Initialization
    // =====================

    private void init(Context context, AttributeSet attrs) {
        // Layout inflate
        LayoutInflater.from(context).inflate(R.layout.rs_badge, this, true);

        // View binding
        rootLayout = findViewById(R.id.rs_badge_root);
        dotView = findViewById(R.id.rs_badge_dot);
        leftIconView = findViewById(R.id.rs_badge_left_icon);
        textView = findViewById(R.id.rs_badge_text);
        rightIconView = findViewById(R.id.rs_badge_right_icon);

        // XML attributes
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RSBadge);
            try {
                currentType = a.getInt(R.styleable.RSBadge_rsBadgeType, TYPE_BASIC);
                currentStyle = a.getInt(R.styleable.RSBadge_rsBadgeStyle, STYLE_LIGHTER);
                currentColor = a.getInt(R.styleable.RSBadge_rsBadgeColor, COLOR_DEFAULT);

                String text = a.getString(R.styleable.RSBadge_rsBadgeText);
                if (text != null) {
                    setText(text);
                }

                number = a.getInt(R.styleable.RSBadge_rsBadgeNumber, -1);
                iconRes = a.getResourceId(R.styleable.RSBadge_rsBadgeIcon, 0);

            } finally {
                a.recycle();
            }
        }

        // Apply initial state
        applyStyle();
    }

    // =====================
    // Style Application
    // =====================

    private void applyStyle() {
        applyBackground();
        applyLayout();
        applyTextColor();
        applyPadding();
    }

    private void applyBackground() {
        if (rootLayout == null)
            return;
        rootLayout.setBackgroundResource(getBackgroundRes());
    }

    @DrawableRes
    private int getBackgroundRes() {
        // Style × Color matrix
        switch (currentStyle) {
            case STYLE_LIGHT:
                switch (currentColor) {
                    case COLOR_BRAND:
                        return R.drawable.rs_badge_bg_brand_light;
                    case COLOR_WARNING:
                        return R.drawable.rs_badge_bg_warning_light;
                    case COLOR_ERROR:
                        return R.drawable.rs_badge_bg_error_light;
                    case COLOR_SUCCESS:
                        return R.drawable.rs_badge_bg_success_light;
                    default:
                        return R.drawable.rs_badge_bg_default_light;
                }
            case STYLE_STROKE:
                switch (currentColor) {
                    case COLOR_BRAND:
                        return R.drawable.rs_badge_bg_brand_stroke;
                    case COLOR_WARNING:
                        return R.drawable.rs_badge_bg_warning_stroke;
                    case COLOR_ERROR:
                        return R.drawable.rs_badge_bg_error_stroke;
                    case COLOR_SUCCESS:
                        return R.drawable.rs_badge_bg_success_stroke;
                    default:
                        return R.drawable.rs_badge_bg_default_stroke;
                }
            case STYLE_LIGHTER:
            default:
                switch (currentColor) {
                    case COLOR_BRAND:
                        return R.drawable.rs_badge_bg_brand_lighter;
                    case COLOR_WARNING:
                        return R.drawable.rs_badge_bg_warning_lighter;
                    case COLOR_ERROR:
                        return R.drawable.rs_badge_bg_error_lighter;
                    case COLOR_SUCCESS:
                        return R.drawable.rs_badge_bg_success_lighter;
                    default:
                        return R.drawable.rs_badge_bg_default_lighter;
                }
        }
    }

    private void applyLayout() {
        if (dotView == null || leftIconView == null || rightIconView == null)
            return;

        // Reset visibility
        dotView.setVisibility(View.GONE);
        leftIconView.setVisibility(View.GONE);
        rightIconView.setVisibility(View.GONE);

        // Number mode: only show number
        if (number > 0) {
            textView.setText(String.valueOf(number));
            return;
        }

        // Apply based on type
        switch (currentType) {
            case TYPE_LEFT_ICON:
                leftIconView.setVisibility(View.VISIBLE);
                if (iconRes != 0) {
                    leftIconView.setImageResource(iconRes);
                }
                leftIconView.setColorFilter(ContextCompat.getColor(getContext(), getTextColorRes()));
                break;

            case TYPE_RIGHT_ICON:
                rightIconView.setVisibility(View.VISIBLE);
                if (iconRes != 0) {
                    rightIconView.setImageResource(iconRes);
                }
                rightIconView.setColorFilter(ContextCompat.getColor(getContext(), getTextColorRes()));
                break;

            case TYPE_WITH_DOT:
                dotView.setVisibility(View.VISIBLE);
                // Create dot drawable
                GradientDrawable dotBg = new GradientDrawable();
                dotBg.setShape(GradientDrawable.OVAL);
                dotBg.setColor(ContextCompat.getColor(getContext(), getTextColorRes()));
                dotView.setBackground(dotBg);
                break;

            case TYPE_BASIC:
            default:
                // No indicator
                break;
        }
    }

    private void applyTextColor() {
        if (textView == null)
            return;
        textView.setTextColor(ContextCompat.getColor(getContext(), getTextColorRes()));
    }

    private int getTextColorRes() {
        switch (currentColor) {
            case COLOR_BRAND:
                return R.color.rs_text_brand_tertiary;
            case COLOR_WARNING:
                return R.color.rs_text_warning_tertiary;
            case COLOR_ERROR:
                return R.color.rs_text_error_tertiary;
            case COLOR_SUCCESS:
                return R.color.rs_text_success_tertiary;
            case COLOR_DEFAULT:
            default:
                return R.color.rs_text_primary;
        }
    }

    private void applyPadding() {
        if (rootLayout == null)
            return;

        int paddingVertical = getResources().getDimensionPixelSize(R.dimen.rs_badge_padding_vertical);
        int paddingStart, paddingEnd;

        // Number mode: uniform padding
        if (number > 0) {
            int paddingNumber = getResources().getDimensionPixelSize(R.dimen.rs_badge_padding_number);
            rootLayout.setPaddingRelative(paddingNumber, paddingNumber, paddingNumber, paddingNumber);
            return;
        }

        // Type-specific padding
        switch (currentType) {
            case TYPE_LEFT_ICON:
                // 2dp 8dp 2dp 4dp
                paddingStart = getResources().getDimensionPixelSize(R.dimen.rs_badge_padding_icon_side);
                paddingEnd = getResources().getDimensionPixelSize(R.dimen.rs_badge_padding_horizontal);
                break;

            case TYPE_RIGHT_ICON:
                // 2dp 4dp 2dp 8dp
                paddingStart = getResources().getDimensionPixelSize(R.dimen.rs_badge_padding_horizontal);
                paddingEnd = getResources().getDimensionPixelSize(R.dimen.rs_badge_padding_icon_side);
                break;

            case TYPE_WITH_DOT:
                // 2dp 8dp 2dp 2dp
                paddingStart = getResources().getDimensionPixelSize(R.dimen.rs_badge_padding_dot_side);
                paddingEnd = getResources().getDimensionPixelSize(R.dimen.rs_badge_padding_horizontal);
                break;

            case TYPE_BASIC:
            default:
                // 2dp 8dp 2dp 8dp
                paddingStart = getResources().getDimensionPixelSize(R.dimen.rs_badge_padding_horizontal);
                paddingEnd = getResources().getDimensionPixelSize(R.dimen.rs_badge_padding_horizontal);
                break;
        }

        rootLayout.setPaddingRelative(paddingStart, paddingVertical, paddingEnd, paddingVertical);
    }

    // =====================
    // Public API - Type
    // =====================

    public void setType(int type) {
        if (type != currentType) {
            currentType = type;
            applyStyle();
        }
    }

    public int getType() {
        return currentType;
    }

    // =====================
    // Public API - Style
    // =====================

    public void setStyle(int style) {
        if (style != currentStyle) {
            currentStyle = style;
            applyStyle();
        }
    }

    public int getStyle() {
        return currentStyle;
    }

    // =====================
    // Public API - Color
    // =====================

    public void setColor(int color) {
        if (color != currentColor) {
            currentColor = color;
            applyStyle();
        }
    }

    public int getColor() {
        return currentColor;
    }

    // =====================
    // Public API - Text
    // =====================

    public void setText(String text) {
        if (textView != null) {
            textView.setText(text);
        }
    }

    public String getText() {
        return textView != null ? textView.getText().toString() : "";
    }

    // =====================
    // Public API - Number
    // =====================

    public void setNumber(int num) {
        number = num;
        applyStyle();
    }

    public int getNumber() {
        return number;
    }

    // =====================
    // Public API - Icon
    // =====================

    public void setIcon(@DrawableRes int iconRes) {
        this.iconRes = iconRes;
        applyLayout();
    }
}
