package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * RealSolutions UI Kit - Edit Text
 *
 * Custom Material-based input component with:
 * - Floating label
 * - Placeholder text
 * - Focus / Error / Success states
 * - Optional end icons (error / success)
 *
 * ----------------------------------------------------
 * Usage (XML):
 *
 * <com.realsolutions.uikit.RSEditText
 *     android:id="@+id/inputSicil"
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     app:rsLabel="Sicil No"
 *     app:rsHint="Sicil numaranızı girin"
 *     app:rsState="normal" />
 *
 * ----------------------------------------------------
 * States:
 *
 * - normal   : Default state (no border, no icon)
 * - focused  : Focused state (blue border)
 * - error    : Error state (red border + error icon)
 * - success  : Success state (neutral border + check icon)
 *
 * ----------------------------------------------------
 * Usage (Java):
 *
 * RSEditText input = findViewById(R.id.inputSicil);
 *
 * // Get / Set text
 * String value = input.getText().toString();
 * input.setText("12345");
 *
 * // State control
 * input.setState(RSEditText.STATE_FOCUSED);
 * input.setErrorState(null);   // red border + error icon (no message)
 * input.setSuccessState();     // success icon
 * input.clearStatus();         // back to normal
 *
 * ----------------------------------------------------
 * Requires:
 *
 * - com.google.android.material:material
 * - res/values/attrs.xml:
 *      <declare-styleable name="RSEditText">
 *          <attr name="rsLabel" format="string"/>
 *          <attr name="rsHint" format="string"/>
 *          <attr name="rsState" format="enum"/>
 *      </declare-styleable>
 * <p>
 * - res/layout/rs_edit_text.xml
 * - Colors & drawables:
 *      rs_input_bg
 *      rs_input_bg_focused
 *      rs_input_stroke_default
 *      rs_input_stroke_focused
 *      rs_input_stroke_error
 *      rs_input_stroke_success
 *      rs_ic_error
 *      rs_ic_success
 *
 * ----------------------------------------------------
 * Notes:
 *
 * - Error text under the input is disabled by design
 * - Visual feedback is provided only via border and icons
 * - Fully compatible with Light / Dark themes
 *
 */
public class RSEditText extends FrameLayout {

    public static final int STATE_DEFAULT = 0;
    public static final int STATE_FOCUSED = 1;
    public static final int STATE_ERROR = 2;
    public static final int STATE_SUCCESS = 3;

    private TextInputLayout til;
    private TextInputEditText et;

    private int state = STATE_DEFAULT;

    public RSEditText(Context context) {
        super(context);
        init(null);
    }

    public RSEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RSEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.rs_edit_text, this, true);
        til = findViewById(R.id.til);
        et = findViewById(R.id.et);

        til.setEndIconVisible(false);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RSEditText);

            String label = a.getString(R.styleable.RSEditText_rsLabel);
            String hint = a.getString(R.styleable.RSEditText_rsHint);
            state = a.getInt(R.styleable.RSEditText_rsState, STATE_DEFAULT);

            a.recycle();

            if (label != null) til.setHint(label);
            if (hint != null) til.setPlaceholderText(hint);
        }

        et.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (state != STATE_ERROR && state != STATE_SUCCESS) {
                    setState(STATE_FOCUSED);
                }
            } else {
                if (state != STATE_ERROR && state != STATE_SUCCESS) {
                    setState(STATE_DEFAULT);
                }
            }
        });

        applyState(state);
    }

    public void setLabel(String label) {
        til.setHint(label);
    }

    public void setHint(String hint) {
        til.setPlaceholderText(hint);
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

    public void setState(int newState) {
        this.state = newState;
        applyState(newState);
    }

    public int getState() {
        return state;
    }

    public void setErrorState(String messageOrNull) {
        til.setError(messageOrNull);
        setState(STATE_ERROR);
    }

    public void setSuccessState() {
        til.setError(null);
        setState(STATE_SUCCESS);
    }

    public void clearStatus() {
        til.setError(null);
        setState(STATE_DEFAULT);
    }

    private void applyState(int st) {
        switch (st) {
            case STATE_FOCUSED:
                til.setBoxStrokeColor(ContextCompat.getColor(getContext(), R.color.rs_input_stroke_focused));
                til.setHintTextColor(ContextCompat.getColorStateList(getContext(), R.color.rs_input_label_focused));
                til.setBoxBackgroundColor(ContextCompat.getColor(getContext(), R.color.rs_input_bg_focused));
                til.setEndIconVisible(false);
                break;

            case STATE_ERROR:
                til.setBoxStrokeColor(ContextCompat.getColor(getContext(), R.color.rs_input_stroke_error));
                til.setHintTextColor(ContextCompat.getColorStateList(getContext(), R.color.rs_input_label_error));
                til.setBoxBackgroundColor(ContextCompat.getColor(getContext(), R.color.rs_input_bg));
                til.setEndIconDrawable(rsDrawable(R.drawable.rs_ic_error));
                til.setEndIconTintList(ContextCompat.getColorStateList(getContext(), R.color.rs_input_stroke_error));
                til.setEndIconVisible(true);
                break;

            case STATE_SUCCESS:
                til.setBoxStrokeColor(ContextCompat.getColor(getContext(), R.color.rs_input_stroke_default));
                til.setHintTextColor(ContextCompat.getColorStateList(getContext(), R.color.rs_input_label_default));
                til.setBoxBackgroundColor(ContextCompat.getColor(getContext(), R.color.rs_input_bg));
                til.setEndIconDrawable(rsDrawable(R.drawable.rs_ic_success));
                til.setEndIconTintList(ContextCompat.getColorStateList(getContext(), R.color.rs_input_stroke_success));
                til.setEndIconVisible(true);
                break;

            case STATE_DEFAULT:
            default:
                til.setBoxStrokeColor(ContextCompat.getColor(getContext(), R.color.rs_input_stroke_default));
                til.setHintTextColor(ContextCompat.getColorStateList(getContext(), R.color.rs_input_label_default));
                til.setBoxBackgroundColor(ContextCompat.getColor(getContext(), R.color.rs_input_bg));
                til.setEndIconVisible(false);
                break;
        }
    }

    private Drawable rsDrawable(int resId) {
        return ContextCompat.getDrawable(getContext(), resId);
    }
}
