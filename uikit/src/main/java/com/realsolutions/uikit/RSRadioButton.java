package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.widget.CompoundButtonCompat;

/**
 * RealSolutions UI Kit RadioButton
 * <p>
 * Figma tasarımına birebir uygun RadioButton bileşeni.
 * <p>
 * - Sizes:
 * sm = 16x16 (2px stroke)
 * md = 20x20 (3px stroke)
 * <p>
 * - States:
 * unchecked / checked
 * default / hover / focus / disabled
 * <p>
 * Usage (XML):
 * <com.realsolutions.uikit.RSRadioButton
 * android:layout_width="wrap_content"
 * android:layout_height="wrap_content"
 * app:rsSize="md" />
 * <p>
 * Usage (Java):
 * RSRadioButton radio = findViewById(R.id.radio);
 * radio.setSize(RSRadioButton.SIZE_SM);
 * radio.setChecked(true);
 * <p>
 * Requires drawables:
 * - rs_radio_button_sm.xml (selector)
 * - rs_radio_button_md.xml (selector)
 * <p>
 * Design Specs:
 * SM: 16x16, stroke 2px, inner dot border 11px when checked
 * MD: 20x20, stroke 3px, inner dot border 14px when checked
 * <p>
 * Default Unchecked: border = rs-border-primary
 * Checked: border = rs-border-brand-solid_alt, bg = rs-bg-primary
 * Hover Checked: border = rs-border-brand-solid
 * Focus: dual ring (spread 2 + spread 4)
 * Disabled: bg = rs-bg-disabled_alt (unchecked) or rs-bg-secondary (checked)
 */
public class RSRadioButton extends AppCompatRadioButton {

    public static final int SIZE_SM = 0;
    public static final int SIZE_MD = 1;

    private int size = SIZE_MD;

    private String text;

    public RSRadioButton(Context context) {
        super(context);
        init(null);
    }

    public RSRadioButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RSRadioButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        // Varsayılan olarak metni boş tut (sadece radio gösterilsin)
        setText(null);

        // Tint'i kapat (drawable renklerini bozmasın)
        CompoundButtonCompat.setButtonTintList(this, null);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RSRadioButton);
            size = a.getInt(R.styleable.RSRadioButton_rsSize, SIZE_MD);
            text = a.getText(R.styleable.RSRadioButton_android_text).toString();
            setText(text);
            a.recycle();
        }

        applyDrawableForSize();
    }

    /**
     * Boyuta göre uygun drawable'ı uygula
     */
    private void applyDrawableForSize() {
        int drawableRes;
        int sizePx;

        if (size == SIZE_SM) {
            drawableRes = R.drawable.rs_radio_button_sm;
            sizePx = dp(16);
        } else {
            drawableRes = R.drawable.rs_radio_button_md;
            sizePx = dp(20);
        }

        setButtonDrawable(drawableRes);

        // Minimum boyutları garantiye al
        setMinWidth(sizePx);
        setMinHeight(sizePx);
    }

    /**
     * Boyutu programatik olarak değiştir
     */
    public void setSize(int newSize) {
        if (newSize != SIZE_SM && newSize != SIZE_MD) {
            newSize = SIZE_MD;
        }
        this.size = newSize;
        applyDrawableForSize();
        invalidate();
        requestLayout();
    }

    /**
     * Mevcut boyutu döndür
     */
    public int getSize() {
        return size;
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }
}
