package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.widget.CompoundButtonCompat;

/**
 * RealSolutions UI Kit CheckBox
 *
 * - Sizes:
 *   sm = 16x16
 *   md = 24x24
 *
 * - Status:
 *   unchecked / checked / indeterminate
 *
 * Usage (XML):
 *  <com.realsolutions.uikit.RSCheckBox
 *      android:layout_width="wrap_content"
 *      android:layout_height="wrap_content"
 *      app:rsSize="md"
 *      app:rsStatus="indeterminate" />
 *
 * Requires drawables:
 *  - rs_checkbox_unchecked_sm.xml
 *  - rs_checkbox_checked_sm.xml
 *  - rs_checkbox_indeterminate_sm.xml
 *  - rs_checkbox_unchecked_md.xml
 *  - rs_checkbox_checked_md.xml
 *  - rs_checkbox_indeterminate_md.xml
 *
 * And icons:
 *  - rs_ic_check.xml
 *  - rs_ic_minus.xml
 */
public class RSCheckBox extends AppCompatCheckBox {

    public static final int SIZE_SM = 0;
    public static final int SIZE_MD = 1;

    public static final int STATUS_UNCHECKED = 0;
    public static final int STATUS_CHECKED = 1;
    public static final int STATUS_INDETERMINATE = 2;

    private int size = SIZE_MD;
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

        // default: metni yok sayalım gibi davran (istersen kaldırırsın)
        setText(null);

        // tint kapat (drawable renklerini bozmasın)
        CompoundButtonCompat.setButtonTintList(this, null);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RSCheckBox);
            size = a.getInt(R.styleable.RSCheckBox_rsSize, SIZE_MD);
            status = a.getInt(R.styleable.RSCheckBox_rsStatus, STATUS_UNCHECKED);
            a.recycle();
        }

        applyDrawableForState();
        syncAndroidCheckedFlag();
    }

    private void syncAndroidCheckedFlag() {
        // Android "checked" sadece checked iken true
        super.setChecked(status == STATUS_CHECKED);
        refreshDrawableState();
    }

    private void applyDrawableForState() {
        int resId;

        if (size == SIZE_SM) {
            if (status == STATUS_CHECKED) resId = R.drawable.rs_checkbox_checked_sm;
            else if (status == STATUS_INDETERMINATE) resId = R.drawable.rs_checkbox_indeterminate_sm;
            else resId = R.drawable.rs_checkbox_unchecked_sm;
        } else {
            if (status == STATUS_CHECKED) resId = R.drawable.rs_checkbox_checked_md;
            else if (status == STATUS_INDETERMINATE) resId = R.drawable.rs_checkbox_indeterminate_md;
            else resId = R.drawable.rs_checkbox_unchecked_md;
        }

        setButtonDrawable(resId);

        // boyutları garantiye alalım (wrap_content olsa bile)
        int px = dp(size == SIZE_SM ? 16 : 24);
        setMinWidth(px);
        setMinHeight(px);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int newStatus) {
        if (newStatus != STATUS_UNCHECKED && newStatus != STATUS_CHECKED && newStatus != STATUS_INDETERMINATE) {
            newStatus = STATUS_UNCHECKED;
        }
        status = newStatus;
        applyDrawableForState();
        syncAndroidCheckedFlag();
    }

    public void setIndeterminate() {
        setStatus(STATUS_INDETERMINATE);
    }

    @Override
    public void setChecked(boolean checked) {
        // dışarıdan setChecked gelirse sadece checked/unchecked yap
        setStatus(checked ? STATUS_CHECKED : STATUS_UNCHECKED);
    }

    @Override
    public boolean performClick() {
        // 3'lü döngü:
        // unchecked -> checked -> indeterminate -> unchecked
        if (status == STATUS_UNCHECKED) setStatus(STATUS_CHECKED);
        else if (status == STATUS_CHECKED) setStatus(STATUS_INDETERMINATE);
        else setStatus(STATUS_UNCHECKED);

        // super çağır (accessibility vs)
        return super.performClick();
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }
}
