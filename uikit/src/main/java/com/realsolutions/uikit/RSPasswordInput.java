package com.realsolutions.uikit;

import android.content.res.TypedArray;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * RealSolutions UI Kit - Password Input
 *
 * Material Design tabanlı, şifre gizleme / gösterme destekli özel input bileşeni.
 * RSEditText altyapısını kullanır.
 *
 * Features:
 *  - Floating label (rsLabel)
 *  - Placeholder text (rsHint)
 *  - Password mask (•••••)
 *  - Show / Hide password icon
 *  - Focus / Error / Success state desteği
 *  - Error message göstermez (sadece görsel geri bildirim)
 *
 * Usage (XML):
 *
 * <com.realsolutions.uikit.RSPasswordInput
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     app:rsLabel="Parola"
 *     app:rsHint="Parolanızı girin"
 *     app:rsState="normal" />
 *
 * Usage (Java):
 *
 * RSPasswordInput input = findViewById(R.id.inputPassword);
 *
 * String value = input.getText().toString();
 *
 * input.setState(RSPasswordInput.STATE_FOCUSED);
 * input.setErrorState(null);
 * input.setSuccessState();
 * input.clearStatus();
 *
 * Requires:
 *  - Material Components
 *  - attrs.xml:
 *      <attr name="rsLabel" format="string"/>
 *      <attr name="rsHint" format="string"/>
 *      <attr name="rsState" format="enum"/>
 *
 * Visual States:
 *  - STATE_DEFAULT  : Varsayılan görünüm
 *  - STATE_FOCUSED  : Fokus (mavi border)
 *  - STATE_ERROR    : Hatalı durum (kırmızı border + icon)
 *  - STATE_SUCCESS  : Başarılı durum (tik iconu)
 */

public class RSPasswordInput extends FrameLayout {

    public static final int STATE_DEFAULT = 0;
    public static final int STATE_FOCUSED = 1;
    public static final int STATE_ERROR = 2;
    public static final int STATE_SUCCESS = 3;

    private TextInputLayout til;
    private TextInputEditText et;

    private int state = STATE_DEFAULT;

    public RSPasswordInput(android.content.Context context) {
        super(context);
        init(null);
    }

    public RSPasswordInput(android.content.Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RSPasswordInput(android.content.Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.rs_password_input, this, true);
        til = findViewById(R.id.til);
        et = findViewById(R.id.et);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RSPasswordInput);

            String label = a.getString(R.styleable.RSPasswordInput_rsLabel);
            String hint = a.getString(R.styleable.RSPasswordInput_rsHint);
            state = a.getInt(R.styleable.RSPasswordInput_rsState, STATE_DEFAULT);

            a.recycle();

            if (label != null) til.setHint(label);
            if (hint != null) til.setPlaceholderText(hint);
        }

        et.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (state != STATE_ERROR && state != STATE_SUCCESS) setState(STATE_FOCUSED);
            } else {
                if (state != STATE_ERROR && state != STATE_SUCCESS) setState(STATE_DEFAULT);
            }
        });

        applyState(state);
    }

    public Editable getText() {
        return et.getText();
    }

    public void setText(String text) {
        et.setText(text);
    }

    public TextInputEditText getEditText() {
        return et;
    }

    public void setLabel(String label) {
        til.setHint(label);
    }

    public void setHint(String hint) {
        til.setPlaceholderText(hint);
    }

    public void setState(int newState) {
        this.state = newState;
        applyState(newState);
    }

    public void setErrorState() {
        setState(STATE_ERROR);
    }

    public void setSuccessState() {
        setState(STATE_SUCCESS);
    }

    public void clearStatus() {
        setState(STATE_DEFAULT);
    }

    private void applyState(int st) {
        switch (st) {
            case STATE_FOCUSED:
                til.setBoxStrokeColor(ContextCompat.getColor(getContext(), R.color.rs_input_stroke_focused));
                til.setHintTextColor(ContextCompat.getColorStateList(getContext(), R.color.rs_input_label_focused));
                til.setBoxBackgroundColor(ContextCompat.getColor(getContext(), R.color.rs_input_bg_focused));
                til.setEndIconTintList(ContextCompat.getColorStateList(getContext(), R.color.rs_input_icon_default));
                break;

            case STATE_ERROR:
                til.setBoxStrokeColor(ContextCompat.getColor(getContext(), R.color.rs_input_stroke_error));
                til.setHintTextColor(ContextCompat.getColorStateList(getContext(), R.color.rs_input_label_error));
                til.setBoxBackgroundColor(ContextCompat.getColor(getContext(), R.color.rs_input_bg));
                til.setEndIconTintList(ContextCompat.getColorStateList(getContext(), R.color.rs_input_icon_default));
                break;

            case STATE_SUCCESS:
                til.setBoxStrokeColor(ContextCompat.getColor(getContext(), R.color.rs_input_stroke_success));
                til.setHintTextColor(ContextCompat.getColorStateList(getContext(), R.color.rs_input_label_default));
                til.setBoxBackgroundColor(ContextCompat.getColor(getContext(), R.color.rs_input_bg));
                til.setEndIconTintList(ContextCompat.getColorStateList(getContext(), R.color.rs_input_icon_default));
                break;

            case STATE_DEFAULT:
            default:
                til.setBoxStrokeColor(ContextCompat.getColor(getContext(), R.color.rs_input_stroke_default));
                til.setHintTextColor(ContextCompat.getColorStateList(getContext(), R.color.rs_input_label_default));
                til.setBoxBackgroundColor(ContextCompat.getColor(getContext(), R.color.rs_input_bg));
                til.setEndIconTintList(ContextCompat.getColorStateList(getContext(), R.color.rs_input_icon_default));
                break;
        }
    }
}
