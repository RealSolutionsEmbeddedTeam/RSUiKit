package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;

/**
 * RealSolutions UI Kit CheckBox
 *
 * Usage (XML):
 *  <com.realsolutions.uikit.RSCheckBox
 *      android:layout_width="wrap_content"
 *      android:layout_height="wrap_content"
 *      android:text="Deneme"
 *      app:rsSize="sm" />
 *
 * Requires:
 *  - res/values/attrs.xml with <attr name="rsSize" .../>
 *  - drawable: rs_checkbox_button
 */
public class RSCheckBox extends AppCompatCheckBox {

    // Must match attrs.xml values
    public static final int SIZE_SM = 0;
    public static final int SIZE_MD = 1;

    private int size = SIZE_SM;

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
        // Read rsSize from XML
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RSCheckBox);
            size = a.getInt(R.styleable.RSCheckBox_rsSize, SIZE_SM);
            a.recycle();
        }

        applyStyle();
        applySize(size);
    }

    /**
     * Apply RSUiKit styling - custom checkbox drawable.
     */
    private void applyStyle() {
        // Apply custom checkbox drawable (Figma tasarımına uygun)
        Drawable buttonDrawable = ContextCompat.getDrawable(getContext(), R.drawable.rs_checkbox_button);
        setButtonDrawable(buttonDrawable);

        // Remove any tint that might override our colors
        CompoundButtonCompat.setButtonTintList(this, null);

        // Ensure proper padding between checkbox and text
        int paddingStart = dp(8);
        setCompoundDrawablePadding(paddingStart);
    }

    /**
     * Apply size programmatically.
     */
    public void applySize(int size) {
        this.size = size;

        // Checkbox size is controlled by the drawable intrinsic size
        // We just need to ensure proper min dimensions for touch target
        int minTouchTarget = dp(48);
        setMinHeight(minTouchTarget);
        setMinWidth(minTouchTarget);

        // Make sure it redraws
        invalidate();
        requestLayout();
    }

    /**
     * Get current size.
     */
    public int getSize() {
        return size;
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
