package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

/**
 * RSTag - Etiket bileşeni.
 * <p>
 * Figma Specs:
 * - Type: default (solid bg), subdued (light bg)
 * - Variant: informative, success, warning, critical, neutral
 * - Icon: optional leading icon
 * <p>
 * Usage:
 * <com.realsolutions.uikit.RSTag
 * app:rsTagType="tag_default"
 * app:rsTagVariant="informative"
 * app:rsTagText="Tag"
 * app:rsTagShowIcon="true" />
 */
public class RSTag extends LinearLayout {

    // =====================
    // Constants - Type
    // =====================

    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_SUBDUED = 1;

    // =====================
    // Constants - Variant
    // =====================

    public static final int VARIANT_INFORMATIVE = 0;
    public static final int VARIANT_SUCCESS = 1;
    public static final int VARIANT_WARNING = 2;
    public static final int VARIANT_CRITICAL = 3;
    public static final int VARIANT_NEUTRAL = 4;

    // =====================
    // Views
    // =====================

    private LinearLayout rootLayout;
    private ImageView iconView;
    private TextView textView;

    // =====================
    // State
    // =====================

    private int currentType = TYPE_DEFAULT;
    private int currentVariant = VARIANT_INFORMATIVE;
    private boolean showIcon = false;
    private int customIconRes = 0;

    // =====================
    // Constructors
    // =====================

    public RSTag(Context context) {
        super(context);
        init(context, null);
    }

    public RSTag(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RSTag(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // =====================
    // Initialization
    // =====================

    private void init(Context context, AttributeSet attrs) {
        // Layout inflate
        LayoutInflater.from(context).inflate(R.layout.rs_tag, this, true);

        // View binding
        rootLayout = findViewById(R.id.rs_tag_root);
        iconView = findViewById(R.id.rs_tag_icon);
        textView = findViewById(R.id.rs_tag_text);

        // XML attributes
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RSTag);
            try {
                // Type
                int type = a.getInt(R.styleable.RSTag_rsTagType, TYPE_DEFAULT);
                currentType = type;

                // Variant
                int variant = a.getInt(R.styleable.RSTag_rsTagVariant, VARIANT_INFORMATIVE);
                currentVariant = variant;

                // Text
                String text = a.getString(R.styleable.RSTag_rsTagText);
                if (text != null) {
                    setText(text);
                }

                // Show icon
                showIcon = a.getBoolean(R.styleable.RSTag_rsTagShowIcon, false);

                // Custom icon
                if (a.hasValue(R.styleable.RSTag_rsTagIcon)) {
                    customIconRes = a.getResourceId(R.styleable.RSTag_rsTagIcon, 0);
                }

            } finally {
                a.recycle();
            }
        }

        // Apply initial state
        applyStyle();
        updateIconVisibility();
    }

    // =====================
    // Style Application
    // =====================

    private void applyStyle() {
        if (rootLayout == null)
            return;

        // Set background
        rootLayout.setBackgroundResource(getBackgroundRes());

        // Set text color
        if (textView != null) {
            textView.setTextColor(getResources().getColor(getTextColorRes(), null));
        }

        // Set icon tint
        if (iconView != null) {
            iconView.setColorFilter(getResources().getColor(getTextColorRes(), null));
        }

        // Adjust padding based on icon visibility
        updatePadding();
    }

    private int getBackgroundRes() {
        switch (currentVariant) {
            case VARIANT_SUCCESS:
                return currentType == TYPE_DEFAULT
                        ? R.drawable.rs_tag_bg_success_default
                        : R.drawable.rs_tag_bg_success_subdued;

            case VARIANT_WARNING:
                return currentType == TYPE_DEFAULT
                        ? R.drawable.rs_tag_bg_warning_default
                        : R.drawable.rs_tag_bg_warning_subdued;

            case VARIANT_CRITICAL:
                return currentType == TYPE_DEFAULT
                        ? R.drawable.rs_tag_bg_critical_default
                        : R.drawable.rs_tag_bg_critical_subdued;

            case VARIANT_NEUTRAL:
                return currentType == TYPE_DEFAULT
                        ? R.drawable.rs_tag_bg_neutral_default
                        : R.drawable.rs_tag_bg_neutral_subdued;

            case VARIANT_INFORMATIVE:
            default:
                return currentType == TYPE_DEFAULT
                        ? R.drawable.rs_tag_bg_informative_default
                        : R.drawable.rs_tag_bg_informative_subdued;
        }
    }

    private int getTextColorRes() {
        // Default type always uses white text
        if (currentType == TYPE_DEFAULT) {
            return R.color.rs_text_white;
        }

        // Subdued type uses variant-specific colors
        switch (currentVariant) {
            case VARIANT_SUCCESS:
                return R.color.rs_text_success_solid;

            case VARIANT_WARNING:
                return R.color.rs_text_warning_tertiary;

            case VARIANT_CRITICAL:
                return R.color.rs_text_error_tertiary;

            case VARIANT_NEUTRAL:
                return R.color.rs_text_primary;

            case VARIANT_INFORMATIVE:
            default:
                return R.color.rs_text_brand_tertiary;
        }
    }

    private void updatePadding() {
        if (rootLayout == null)
            return;

        int paddingVertical = getResources().getDimensionPixelSize(R.dimen.rs_tag_padding_vertical);
        int paddingEnd = getResources().getDimensionPixelSize(R.dimen.rs_tag_padding_horizontal);
        int paddingStart = showIcon
                ? getResources().getDimensionPixelSize(R.dimen.rs_tag_padding_start_with_icon)
                : getResources().getDimensionPixelSize(R.dimen.rs_tag_padding_horizontal);

        rootLayout.setPaddingRelative(paddingStart, paddingVertical, paddingEnd, paddingVertical);
    }

    private void updateIconVisibility() {
        if (iconView == null)
            return;

        iconView.setVisibility(showIcon ? View.VISIBLE : View.GONE);

        // Set icon - custom or default
        if (showIcon) {
            if (customIconRes != 0) {
                iconView.setImageResource(customIconRes);
            } else {
                // Use default tag icon
                iconView.setImageResource(R.drawable.rs_ic_tag);
            }
        }

        // Update padding when icon visibility changes
        updatePadding();
    }

    // =====================
    // Public API - Type
    // =====================

    /**
     * Tag tipini ayarlar.
     *
     * @param type TYPE_DEFAULT veya TYPE_SUBDUED
     */
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
    // Public API - Variant
    // =====================

    /**
     * Tag varyantını ayarlar.
     *
     * @param variant VARIANT_INFORMATIVE, VARIANT_SUCCESS, vb.
     */
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
    // Public API - Text
    // =====================

    /**
     * Tag metnini ayarlar.
     *
     * @param text Gösterilecek metin
     */
    public void setText(String text) {
        if (textView != null) {
            textView.setText(text);
        }
    }

    public String getText() {
        return textView != null ? textView.getText().toString() : "";
    }

    // =====================
    // Public API - Icon
    // =====================

    /**
     * İkon görünürlüğünü ayarlar.
     *
     * @param show true ise ikon görünür
     */
    public void setShowIcon(boolean show) {
        if (show != showIcon) {
            showIcon = show;
            updateIconVisibility();
        }
    }

    public boolean isShowIcon() {
        return showIcon;
    }

    /**
     * Özel ikon resource'u ayarlar.
     *
     * @param iconRes Drawable resource ID
     */
    public void setIcon(@DrawableRes int iconRes) {
        customIconRes = iconRes;
        if (iconView != null && showIcon) {
            iconView.setImageResource(iconRes);
        }
    }
}
