package com.realsolutions.uikit.select;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.realsolutions.uikit.R;

import java.util.ArrayList;
import java.util.List;

public class RSSelect extends LinearLayout {

    // Sizin RSState enum değerleri ile aynı:
    public static final int STATE_NORMAL = 0;
    public static final int STATE_FOCUSED = 1;
    public static final int STATE_ERROR = 2;
    public static final int STATE_SUCCESS = 3;

    public interface OnItemSelectedListener {
        void onItemSelected(int index, @NonNull String value);
    }

    private LinearLayout root;
    private ImageView leading;
    private TextView text;
    private ImageView chevron;

    private final List<String> items = new ArrayList<>();
    private ListPopupWindow popup;

    private String placeholder = "Select...";
    private @Nullable String value = null;

    private int rsState = STATE_NORMAL;
    private boolean rsEnabled = true;

    // colors (token isimlerini kendi palette göre değiştir)
    private int cTextPrimary;
    private int cTextPlaceholder;
    private int cTextDisabled;

    private int cBg;
    private int cBgDisabled;

    private int cStrokeNormal;
    private int cStrokeFocused;
    private int cStrokeError;
    private int cStrokeSuccess;
    private int cStrokeDisabled;

    private int cChevron;
    private RSSelectStringAdapter adapter;

    @Nullable
    private OnItemSelectedListener listener;

    public RSSelect(@NonNull Context context) {
        this(context, null);
    }

    public RSSelect(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setClickable(true);
        setFocusable(true);

        LayoutInflater.from(context).inflate(R.layout.rs_select, this, true);

        root = findViewById(R.id.rsSelectRoot);
        leading = findViewById(R.id.rsSelectLeading);
        text = findViewById(R.id.rsSelectText);
        chevron = findViewById(R.id.rsSelectChevron);

        // ---- tokens (RSUiKit paletine göre düzenle) ----
        cTextPrimary = ContextCompat.getColor(context, R.color.rs_text_primary);
        cTextPlaceholder = ContextCompat.getColor(context, R.color.rs_text_tertiary);
        cTextDisabled = ContextCompat.getColor(context, R.color.rs_text_tertiary);

        cBg = ContextCompat.getColor(context, R.color.rs_base_white);
        cBgDisabled = ContextCompat.getColor(context, R.color.rs_bg_disabled); // yoksa gray_50 gibi bir şey

        cStrokeNormal = ContextCompat.getColor(context, R.color.rs_text_secondary);
        cStrokeFocused = ContextCompat.getColor(context, R.color.rs_text_brand_solid);
        cStrokeError = ContextCompat.getColor(context, R.color.rs_text_error_solid);
        cStrokeSuccess = ContextCompat.getColor(context, R.color.rs_text_success_solid);
        cStrokeDisabled = ContextCompat.getColor(context, R.color.rs_border_tertiary);

        cChevron = ContextCompat.getColor(context, R.color.rs_text_secondary);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RSSelect);

            String ph = a.getString(R.styleable.RSSelect_rsPlaceholder);
            if (ph != null && !ph.trim().isEmpty()) placeholder = ph;

            rsState = a.getInt(R.styleable.RSSelect_rsState, STATE_NORMAL);

            rsEnabled = a.getBoolean(R.styleable.RSSelect_rsEnabled, true);

            int leadingRes = a.getResourceId(R.styleable.RSSelect_rsLeadingIcon, 0);
            if (leadingRes != 0) setLeadingIcon(leadingRes);
            else setLeadingVisible(false);

            a.recycle();
        } else {
            setLeadingVisible(false);
        }

        // open dropdown on click
        root.setOnClickListener(v -> {
            if (!rsEnabled) return;
            showDropdown();
        });
        setOnClickListener(v -> {
            if (!rsEnabled) return;
            showDropdown();
        });

        applyText();
        applyStyle();
        setupPopup();
    }

    // ---------------- Public API ----------------

    public void setOnItemSelectedListener(@Nullable OnItemSelectedListener l) {
        this.listener = l;
    }

    public void setItems(@NonNull List<String> list) {
        items.clear();
        items.addAll(list);

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            setupPopup();
        }
    }

    public void setState(int state) {
        rsState = state;
        applyStyle();
    }

    public void setRsEnabled(boolean enabled) {
        rsEnabled = enabled;
        setEnabled(enabled);
        applyStyle();
        applyText();
    }

    public void clearSelection() {
        value = null;
        applyText();
    }

    @Nullable
    public String getValue() {
        return value;
    }

    public void setValue(@Nullable String v) {
        value = v;
        applyText();
    }

    public void setPlaceholder(@NonNull String ph) {
        placeholder = ph;
        applyText();
    }

    public void setLeadingVisible(boolean visible) {
        leading.setVisibility(visible ? VISIBLE : GONE);
        // text margin start zaten var; ikon yoksa da problem değil
    }

    public void setLeadingIcon(@DrawableRes int res) {
        leading.setImageResource(res);
        leading.setVisibility(VISIBLE);
    }

    // avatar desteği istersen:
    // public void setLeadingBitmap(Bitmap bmp) { ... }

    // ---------------- Internal ----------------

    private void setupPopup() {
        popup = new ListPopupWindow(getContext());
        popup.setAnchorView(this);
        popup.setModal(true);
        popup.setWidth(ListPopupWindow.WRAP_CONTENT);

        adapter = new RSSelectStringAdapter(getContext(), items);
        popup.setAdapter(adapter);

        popup.setOnItemClickListener((parent, view, position, id) -> {
            String selected = items.get(position);
            value = selected;
            applyText();
            popup.dismiss();

            if (listener != null) {
                listener.onItemSelected(position, selected);
            }
        });

        popup.setOnDismissListener(() -> {
            if (rsState == STATE_FOCUSED) {
                setState(STATE_NORMAL);
            }
        });
    }


    private void showDropdown() {
        if (items.isEmpty()) return;

        setState(STATE_FOCUSED);

        int w = getWidth();
        if (w <= 0) w = root.getWidth();
        popup.setContentWidth(w);

        popup.show();
    }

    private void applyText() {
        boolean hasValue = value != null && !value.trim().isEmpty();

        if (!rsEnabled) {
            text.setText(hasValue ? value : placeholder);
            text.setTextColor(cTextDisabled);
            text.setAlpha(0.55f);
            return;
        }

        text.setAlpha(1f);
        if (hasValue) {
            text.setText(value);
            text.setTextColor(cTextPrimary);
        } else {
            text.setText(placeholder);
            text.setTextColor(cTextPlaceholder);
        }
    }

    private void applyStyle() {
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(10));

        int stroke;
        int fill;

        if (!rsEnabled) {
            stroke = cStrokeDisabled;
            fill = cBgDisabled;
        } else {
            fill = cBg;
            switch (rsState) {
                case STATE_ERROR:
                    stroke = cStrokeError;
                    break;
                case STATE_SUCCESS:
                    stroke = cStrokeSuccess;
                    break;
                case STATE_FOCUSED:
                    stroke = cStrokeFocused;
                    break;
                case STATE_NORMAL:
                default:
                    stroke = cStrokeNormal;
                    break;
            }
        }

        bg.setColor(fill);
        bg.setStroke(dp(1), stroke);
        root.setBackground(bg);

        chevron.setColorFilter(cChevron);
        leading.setAlpha(rsEnabled ? 1f : 0.55f);
        chevron.setAlpha(rsEnabled ? 1f : 0.55f);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
