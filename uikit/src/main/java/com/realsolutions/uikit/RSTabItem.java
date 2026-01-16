package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
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
 * RSTabItem - Tab button component.
 * <p>
 * Figma Specs:
 * - Type: full_circle (pill-shaped background), default (underline only)
 * - Size: small, medium
 * - States: unselected, selected, hover, focus
 * - Optional: leading icon, trailing counter
 * <p>
 * Usage:
 * <com.realsolutions.uikit.RSTabItem
 * app:rsTabType="full_circle"
 * app:rsTabSize="medium"
 * app:rsTabLabel="Label"
 * app:rsTabIcon="@drawable/ic_icon"
 * app:rsTabCounter="4"
 * app:rsTabSelected="true" />
 */
public class RSTabItem extends LinearLayout {

    // =====================
    // Constants - Type
    // =====================

    public static final int TYPE_FULL_CIRCLE = 0;
    public static final int TYPE_UNDERLINE = 1;

    // =====================
    // Constants - Size
    // =====================

    public static final int SIZE_SMALL = 0;
    public static final int SIZE_MEDIUM = 1;

    // =====================
    // Constants - State
    // =====================

    public static final int STATE_NORMAL = 0;
    public static final int STATE_HOVER = 1;
    public static final int STATE_FOCUS = 2;
    public static final int STATE_SELECTED = 3;

    // =====================
    // Views
    // =====================

    private ImageView iconView;
    private TextView labelView;
    private TextView counterView;
    private View underlineView;

    // =====================
    // State
    // =====================

    private int currentType = TYPE_FULL_CIRCLE;
    private int currentSize = SIZE_SMALL;
    private int currentState = STATE_NORMAL;
    private int iconRes = 0;
    private int counter = -1; // -1 means hidden
    private boolean iconVisible = true;
    private boolean counterVisible = true;

    // =====================
    // Constructors
    // =====================

    public RSTabItem(Context context) {
        super(context);
        init(context, null);
    }

    public RSTabItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RSTabItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // =====================
    // Initialization
    // =====================

    private void init(Context context, AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        setGravity(android.view.Gravity.CENTER);
        setClickable(true);
        setFocusable(true);

        // Inflate layout and get the root
        View inflatedView = LayoutInflater.from(context).inflate(R.layout.rs_tab_item, this, true);

        // Bind views - they are children of the inflated LinearLayout
        iconView = inflatedView.findViewById(R.id.rs_tab_item_icon);
        labelView = inflatedView.findViewById(R.id.rs_tab_item_label);
        counterView = inflatedView.findViewById(R.id.rs_tab_item_counter);
        underlineView = inflatedView.findViewById(R.id.rs_tab_item_underline);

        // Parse XML attributes
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RSTabItem);
            try {
                currentType = a.getInt(R.styleable.RSTabItem_rsTabType, TYPE_FULL_CIRCLE);
                currentSize = a.getInt(R.styleable.RSTabItem_rsTabSize, SIZE_SMALL);
                currentState = a.getInt(R.styleable.RSTabItem_rsTabState, STATE_NORMAL);

                String label = a.getString(R.styleable.RSTabItem_rsTabLabel);
                if (label != null) {
                    setLabel(label);
                }

                iconRes = a.getResourceId(R.styleable.RSTabItem_rsTabIcon, 0);
                counter = a.getInt(R.styleable.RSTabItem_rsTabCounter, -1);
                iconVisible = a.getBoolean(R.styleable.RSTabItem_rsTabIconVisible, true);
                counterVisible = a.getBoolean(R.styleable.RSTabItem_rsTabCounterVisible, true);
            } finally {
                a.recycle();
            }
        }

        // Apply initial style
        applyStyle();
    }

    // =====================
    // Style Application
    // =====================

    private void applyStyle() {
        applyBackground();
        applyIcon();
        applyLabel();
        applyCounter();
        applyUnderline();
        applyPadding();
        applySizing();
    }

    private void applyBackground() {
        if (currentType == TYPE_FULL_CIRCLE) {
            switch (currentState) {
                case STATE_SELECTED:
                    setBackgroundResource(R.drawable.rs_tab_item_bg_selected);
                    break;
                case STATE_FOCUS:
                    setBackgroundResource(R.drawable.rs_tab_item_bg_focus);
                    break;
                case STATE_HOVER:
                    setBackgroundResource(R.drawable.rs_tab_item_bg_hover);
                    break;
                case STATE_NORMAL:
                default:
                    setBackgroundResource(R.drawable.rs_tab_item_bg_default);
                    break;
            }
        } else {
            // Underline type has transparent background, hover/focus still applies
            switch (currentState) {
                case STATE_FOCUS:
                    setBackgroundResource(R.drawable.rs_tab_item_bg_focus);
                    break;
                case STATE_HOVER:
                    setBackgroundResource(R.drawable.rs_tab_item_bg_hover);
                    break;
                default:
                    setBackgroundResource(R.drawable.rs_tab_item_bg_default);
                    break;
            }
        }
    }

    private void applyIcon() {
        if (iconView == null)
            return;

        if (iconRes != 0 && iconVisible) {
            iconView.setImageResource(iconRes);
            iconView.setVisibility(View.VISIBLE);

            // Apply icon color based on state
            int iconColor = (currentState == STATE_SELECTED && currentType == TYPE_FULL_CIRCLE)
                    ? R.color.rs_text_white
                    : R.color.rs_text_placeholder;
            iconView.setColorFilter(ContextCompat.getColor(getContext(), iconColor));
        } else {
            iconView.setVisibility(View.GONE);
        }
    }

    private void applyLabel() {
        if (labelView == null)
            return;

        // Set text color based on state
        int textColor;
        if (currentState == STATE_SELECTED) {
            textColor = currentType == TYPE_FULL_CIRCLE
                    ? R.color.rs_text_white
                    : R.color.rs_text_primary;
        } else {
            textColor = R.color.rs_text_placeholder;
        }
        labelView.setTextColor(ContextCompat.getColor(getContext(), textColor));

        // Set text size based on size
        int textSizeRes = currentSize == SIZE_MEDIUM
                ? R.dimen.rs_tab_item_text_size_md
                : R.dimen.rs_tab_item_text_size_sm;
        labelView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimensionPixelSize(textSizeRes));
    }

    private void applyCounter() {
        if (counterView == null)
            return;

        if (counter >= 0 && counterVisible) {
            counterView.setText(String.valueOf(counter));
            counterView.setVisibility(View.VISIBLE);

            // Apply counter style based on state
            if (currentState == STATE_SELECTED && currentType == TYPE_FULL_CIRCLE) {
                counterView.setBackgroundResource(R.drawable.rs_status_badge_bg_contained);
                counterView.setTextColor(ContextCompat.getColor(getContext(), R.color.rs_text_white));
            } else {
                counterView.setBackgroundResource(R.drawable.rs_status_badge_bg_outlined);
                counterView.setTextColor(ContextCompat.getColor(getContext(), R.color.rs_text_primary));
            }
        } else {
            counterView.setVisibility(View.GONE);
        }
    }

    private void applyUnderline() {
        if (underlineView == null)
            return;

        // Underline is only visible for underline type when selected
        if (currentType == TYPE_UNDERLINE && currentState == STATE_SELECTED) {
            underlineView.setVisibility(View.VISIBLE);
        } else {
            underlineView.setVisibility(View.GONE);
        }
    }

    private void applyPadding() {
        int paddingTop, paddingBottom, paddingStart, paddingEnd;

        if (currentSize == SIZE_MEDIUM) {
            paddingTop = getResources().getDimensionPixelSize(R.dimen.rs_tab_item_padding_top_md);
            paddingBottom = getResources().getDimensionPixelSize(R.dimen.rs_tab_item_padding_bottom_md);

            if (iconRes != 0) {
                paddingStart = getResources().getDimensionPixelSize(R.dimen.rs_tab_item_padding_start_icon_md);
            } else {
                paddingStart = getResources().getDimensionPixelSize(R.dimen.rs_tab_item_padding_start_md);
            }

            if (counter >= 0) {
                paddingEnd = getResources().getDimensionPixelSize(R.dimen.rs_tab_item_padding_end_counter_md);
            } else {
                paddingEnd = getResources().getDimensionPixelSize(R.dimen.rs_tab_item_padding_end_md);
            }
        } else {
            paddingTop = getResources().getDimensionPixelSize(R.dimen.rs_tab_item_padding_top_sm);
            paddingBottom = getResources().getDimensionPixelSize(R.dimen.rs_tab_item_padding_bottom_sm);

            if (iconRes != 0) {
                paddingStart = getResources().getDimensionPixelSize(R.dimen.rs_tab_item_padding_start_icon_sm);
            } else {
                paddingStart = getResources().getDimensionPixelSize(R.dimen.rs_tab_item_padding_start_sm);
            }

            if (counter >= 0) {
                paddingEnd = getResources().getDimensionPixelSize(R.dimen.rs_tab_item_padding_end_counter_sm);
            } else {
                paddingEnd = getResources().getDimensionPixelSize(R.dimen.rs_tab_item_padding_end_sm);
            }
        }

        setPaddingRelative(paddingStart, paddingTop, paddingEnd, paddingBottom);
    }

    private void applySizing() {
        if (iconView == null)
            return;

        // Set icon size based on size variant
        int iconSize = currentSize == SIZE_MEDIUM
                ? getResources().getDimensionPixelSize(R.dimen.rs_tab_item_icon_size_md)
                : getResources().getDimensionPixelSize(R.dimen.rs_tab_item_icon_size_sm);

        LayoutParams params = (LayoutParams) iconView.getLayoutParams();
        params.width = iconSize;
        params.height = iconSize;
        params.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.rs_tab_item_gap));
        iconView.setLayoutParams(params);
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
    // Public API - Size
    // =====================

    public void setSize(int size) {
        if (size != currentSize) {
            currentSize = size;
            applyStyle();
        }
    }

    public int getSize() {
        return currentSize;
    }

    // =====================
    // Public API - State
    // =====================

    public void setState(int state) {
        if (state != currentState) {
            currentState = state;
            super.setSelected(state == STATE_SELECTED);
            applyStyle();
        }
    }

    public int getState() {
        return currentState;
    }

    @Override
    public void setSelected(boolean selected) {
        setState(selected ? STATE_SELECTED : STATE_NORMAL);
    }

    @Override
    public boolean isSelected() {
        return currentState == STATE_SELECTED;
    }

    // =====================
    // Public API - Label
    // =====================

    public void setLabel(String label) {
        if (labelView != null) {
            labelView.setText(label);
        }
    }

    public String getLabel() {
        return labelView != null ? labelView.getText().toString() : "";
    }

    // =====================
    // Public API - Icon
    // =====================

    public void setIcon(@DrawableRes int resId) {
        iconRes = resId;
        applyIcon();
        applyPadding();
    }

    public void setIcon(Drawable drawable) {
        if (iconView != null) {
            iconView.setImageDrawable(drawable);
            iconView.setVisibility(drawable != null ? View.VISIBLE : View.GONE);
            applyPadding();
        }
    }

    // =====================
    // Public API - Counter
    // =====================

    public void setCounter(int count) {
        counter = count;
        applyCounter();
        applyPadding();
    }

    public int getCounter() {
        return counter;
    }

    public void hideCounter() {
        counter = -1;
        applyCounter();
        applyPadding();
    }
}
