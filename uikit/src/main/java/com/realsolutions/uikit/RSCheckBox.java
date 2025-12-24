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
 * Üç farklı durumu (Checked, Unchecked, Indeterminate) destekleyen,
 * Figma tasarımına sadık özel bileşen.
 */
public class RSCheckBox extends AppCompatCheckBox {

    // Durum Sabitleri (attrs.xml ile uyumlu)
    public static final int STATUS_UNCHECKED = 0;
    public static final int STATUS_CHECKED = 1;
    public static final int STATUS_INDETERMINATE = 2;

    // Selector'da kullanacağımız özel durum dizisi
    private static final int[] STATE_INDETERMINATE = {R.attr.state_indeterminate};

    private int status = STATUS_UNCHECKED;

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
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RSCheckBox);
            status = a.getInt(R.styleable.RSCheckBox_rsStatus, STATUS_UNCHECKED);
            a.recycle();
        }

        applyStyle();
        // Başlangıç durumunu uygula
        setStatus(status);
    }

    private void applyStyle() {
        // Figma tasarımına uygun custom drawable
        Drawable buttonDrawable = ContextCompat.getDrawable(getContext(), R.drawable.rs_checkbox_button);
        setButtonDrawable(buttonDrawable);

        // Herhangi bir tint'in bizim özel renklerimizi ezmesini engelle
        CompoundButtonCompat.setButtonTintList(this, null);

        // Metin ile checkbox arasındaki boşluk (8dp)
        int paddingStart = (int) (8 * getResources().getDisplayMetrics().density);
        setCompoundDrawablePadding(paddingStart);

        // Standart dokunma alanı (min 48dp)
        int minArea = (int) (48 * getResources().getDisplayMetrics().density);
        setMinHeight(minArea);
        setMinWidth(minArea);
    }

    /**
     * Checkbox durumunu ayarlar ve görünümü tazeler.
     * @param status STATUS_UNCHECKED, STATUS_CHECKED veya STATUS_INDETERMINATE
     */
    public void setStatus(int status) {
        this.status = status;

        // Android'in yerleşik 'checked' durumunu senkronize et
        // Sadece STATUS_CHECKED durumunda yerleşik checked true olur
        super.setChecked(status == STATUS_CHECKED);

        // Drawable state'i yenile (onCreateDrawableState tetiklenir)
        refreshDrawableState();
    }

    public int getStatus() {
        return status;
    }

    /**
     * Yerleşik setChecked çağrıldığında durumu otomatik güncelle.
     */
    @Override
    public void setChecked(boolean checked) {
        setStatus(checked ? STATUS_CHECKED : STATUS_UNCHECKED);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        // Eğer durum indeterminate ise, listeye bizim özel state'imizi ekliyoruz
        if (status == STATUS_INDETERMINATE) {
            final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
            mergeDrawableStates(drawableState, STATE_INDETERMINATE);
            return drawableState;
        }
        return super.onCreateDrawableState(extraSpace);
    }

    @Override
    public boolean performClick() {
        // Tıklandığında döngüsel geçiş mantığı
        if (status == STATUS_INDETERMINATE) {
            setStatus(STATUS_CHECKED);
        } else {
            setStatus(status == STATUS_CHECKED ? STATUS_UNCHECKED : STATUS_CHECKED);
        }
        return super.performClick();
    }
}