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

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * RSStatusBadge - Status Badge bileşeni.
 * <p>
 * Figma Specs:
 * - Status: success, warning, error, information, default
 * - Variant: linked (transparent), contained (solid bg), outlined (border)
 * - Type: dot (colored dot), icon (status icon)
 * <p>
 * Usage:
 * <com.realsolutions.uikit.RSStatusBadge
 * app:rsStatusBadgeStatus="success"
 * app:rsStatusBadgeVariant="contained"
 * app:rsStatusBadgeType="icon"
 * app:rsStatusBadgeText="Badge" />
 */
public class RSStatusBadge extends LinearLayout {

    // =====================
    // Constants - Status
    // =====================

    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_WARNING = 1;
    public static final int STATUS_ERROR = 2;
    public static final int STATUS_INFORMATION = 3;
    public static final int STATUS_DEFAULT = 4;

    // =====================
    // Constants - Variant
    // =====================

    public static final int VARIANT_LINKED = 0;
    public static final int VARIANT_CONTAINED = 1;
    public static final int VARIANT_OUTLINED = 2;

    // =====================
    // Constants - Type
    // =====================

    public static final int TYPE_DOT = 0;
    public static final int TYPE_ICON = 1;

    // =====================
    // Views
    // =====================

    private LinearLayout rootLayout;
    private View dotView;
    private ImageView iconView;
    private TextView textView;

    // =====================
    // State
    // =====================

    private int currentStatus = STATUS_SUCCESS;
    private int currentVariant = VARIANT_CONTAINED;
    private int currentType = TYPE_DOT;

    // =====================
    // Constructors
    // =====================

    public RSStatusBadge(Context context) {
        super(context);
        init(context, null);
    }

    public RSStatusBadge(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RSStatusBadge(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // =====================
    // Initialization
    // =====================

    private void init(Context context, AttributeSet attrs) {
        // Layout inflate
        LayoutInflater.from(context).inflate(R.layout.rs_status_badge, this, true);

        // View binding
        rootLayout = findViewById(R.id.rs_status_badge_root);
        dotView = findViewById(R.id.rs_status_badge_dot);
        iconView = findViewById(R.id.rs_status_badge_icon);
        textView = findViewById(R.id.rs_status_badge_text);

        // XML attributes
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RSStatusBadge);
            try {
                currentStatus = a.getInt(R.styleable.RSStatusBadge_rsStatusBadgeStatus, STATUS_SUCCESS);
                currentVariant = a.getInt(R.styleable.RSStatusBadge_rsStatusBadgeVariant, VARIANT_CONTAINED);
                currentType = a.getInt(R.styleable.RSStatusBadge_rsStatusBadgeType, TYPE_DOT);

                String text = a.getString(R.styleable.RSStatusBadge_rsStatusBadgeText);
                if (text != null) {
                    setText(text);
                }
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
        applyIndicator();
        applyTextColor();
        applyPadding();
    }

    private void applyBackground() {
        if (rootLayout == null)
            return;

        switch (currentVariant) {
            case VARIANT_LINKED:
                // Transparent background
                rootLayout.setBackground(null);
                break;

            case VARIANT_OUTLINED:
                // White background with border
                rootLayout.setBackgroundResource(R.drawable.rs_status_badge_bg_outlined);
                break;

            case VARIANT_CONTAINED:
            default:
                // Solid colored background
                GradientDrawable bg = new GradientDrawable();
                bg.setShape(GradientDrawable.RECTANGLE);
                bg.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.rs_status_badge_radius));
                bg.setColor(ContextCompat.getColor(getContext(), getContainedBgColorRes()));
                rootLayout.setBackground(bg);
                break;
        }
    }

    private int getContainedBgColorRes() {
        switch (currentStatus) {
            case STATUS_WARNING:
                return R.color.rs_bg_warning_primary;
            case STATUS_ERROR:
                return R.color.rs_bg_error_primary;
            case STATUS_INFORMATION:
                return R.color.rs_bg_brand_primary;
            case STATUS_DEFAULT:
                return R.color.rs_bg_tertiary;
            case STATUS_SUCCESS:
            default:
                return R.color.rs_bg_success_primary;
        }
    }

    private void applyIndicator() {
        if (dotView == null || iconView == null)
            return;

        int textColorRes = getTextColorRes();

        if (currentType == TYPE_DOT) {
            dotView.setVisibility(View.VISIBLE);
            iconView.setVisibility(View.GONE);

            // Create circular dot
            GradientDrawable dotBg = new GradientDrawable();
            dotBg.setShape(GradientDrawable.OVAL);
            dotBg.setColor(ContextCompat.getColor(getContext(), textColorRes));
            dotView.setBackground(dotBg);
        } else {
            dotView.setVisibility(View.GONE);
            iconView.setVisibility(View.VISIBLE);

            // Set icon based on status
            iconView.setImageResource(getIconRes());
            iconView.setColorFilter(ContextCompat.getColor(getContext(), textColorRes));
        }
    }

    private int getIconRes() {
        switch (currentStatus) {
            case STATUS_WARNING:
                return R.drawable.rs_ic_status_badge_warning;
            case STATUS_ERROR:
                return R.drawable.rs_ic_status_badge_error;
            case STATUS_INFORMATION:
                return R.drawable.rs_ic_status_badge_information;
            case STATUS_DEFAULT:
                return R.drawable.rs_ic_status_badge_default;
            case STATUS_SUCCESS:
            default:
                return R.drawable.rs_ic_status_badge_success;
        }
    }

    private void applyTextColor() {
        if (textView == null)
            return;
        textView.setTextColor(ContextCompat.getColor(getContext(), getTextColorRes()));
    }

    private int getTextColorRes() {
        switch (currentStatus) {
            case STATUS_WARNING:
                return R.color.rs_text_warning_tertiary;
            case STATUS_ERROR:
                return R.color.rs_text_error_tertiary;
            case STATUS_INFORMATION:
                return R.color.rs_text_brand_tertiary;
            case STATUS_DEFAULT:
                return R.color.rs_text_tertiary;
            case STATUS_SUCCESS:
            default:
                return R.color.rs_text_success_tertiary;
        }
    }

    private void applyPadding() {
        if (rootLayout == null)
            return;

        int paddingVertical = getResources().getDimensionPixelSize(R.dimen.rs_status_badge_padding_vertical);
        int paddingEnd = getResources().getDimensionPixelSize(R.dimen.rs_status_badge_padding_end);
        int paddingStart = currentType == TYPE_ICON
                ? getResources().getDimensionPixelSize(R.dimen.rs_status_badge_padding_start_icon)
                : getResources().getDimensionPixelSize(R.dimen.rs_status_badge_padding_start_dot);

        rootLayout.setPaddingRelative(paddingStart, paddingVertical, paddingEnd, paddingVertical);

        // Update gap based on variant
        int gap = currentVariant == VARIANT_LINKED
                ? getResources().getDimensionPixelSize(R.dimen.rs_status_badge_gap_linked)
                : getResources().getDimensionPixelSize(R.dimen.rs_status_badge_gap);

        if (currentType == TYPE_DOT && dotView != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dotView.getLayoutParams();
            params.setMarginEnd(gap);
            dotView.setLayoutParams(params);
        } else if (currentType == TYPE_ICON && iconView != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) iconView.getLayoutParams();
            params.setMarginEnd(gap);
            iconView.setLayoutParams(params);
        }
    }

    // =====================
    // Public API - Status
    // =====================

    public void setStatus(int status) {
        if (status != currentStatus) {
            currentStatus = status;
            applyStyle();
        }
    }

    public int getStatus() {
        return currentStatus;
    }

    // =====================
    // Public API - Variant
    // =====================

    public void setVariant(int variant) {
        if (variant != currentVariant) {
            currentVariant = variant;
            applyStyle();
        }
    }

    public int getVariant() {
        return currentVariant;
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
}
