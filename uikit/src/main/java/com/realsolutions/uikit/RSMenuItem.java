package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * RSMenuItem - Tek bir menü öğesini temsil eden bileşen.
 * <p>
 * Figma Tasarım Özellikleri:
 * - Padding: 12dp
 * - Border-radius: 12dp
 * - Gap (icon-text): 8dp
 * - Icon size: 24dp
 * - Checkmark size: 20dp
 * <p>
 * State'ler:
 * - DEFAULT: Şeffaf arka plan, normal text
 * - HOVER: rs_bg_primary_on_hover arka plan
 * - FOCUS: 2dp rs_border_strong border
 * - SELECTED: Şeffaf arka plan + checkmark ikonu
 * - DISABLED: Şeffaf arka plan, soluk text ve ikon
 * <p>
 * Kullanım (XML):
 *
 * <pre>
 * &lt;com.realsolutions.uikit.RSMenuItem
 *     app:rsMenuItemLabel="Menu Item"
 *     app:rsMenuItemIcon="@drawable/rs_ic_dialog_location"
 *     app:rsMenuItemIconVisible="true"
 *     app:rsMenuItemState="normal" /&gt;
 * </pre>
 * <p>
 * Kullanım (Java):
 *
 * <pre>
 * RSMenuItem item = new RSMenuItem(context);
 * item.setLabel("Menu Item");
 * item.setIcon(R.drawable.rs_ic_dialog_location);
 * item.setIconVisible(true);
 * item.setState(RSMenuItem.STATE_SELECTED);
 * </pre>
 */
public class RSMenuItem extends FrameLayout {

    // =====================
    // State Constants
    // =====================

    /**
     * Varsayılan durum - şeffaf arka plan
     */
    public static final int STATE_DEFAULT = 0;
    /**
     * Hover durumu - rs_bg_primary_on_hover arka plan
     */
    public static final int STATE_HOVER = 1;
    /**
     * Focus durumu - 2dp border
     */
    public static final int STATE_FOCUS = 2;
    /**
     * Seçili durum - checkmark ikonu görünür
     */
    public static final int STATE_SELECTED = 3;
    /**
     * Devre dışı durum - soluk renkler
     */
    public static final int STATE_DISABLED = 4;

    // =====================
    // Views
    // =====================

    private LinearLayout rootLayout;
    private ImageView iconView;
    private TextView labelView;
    private ImageView checkmarkView;

    // =====================
    // State
    // =====================

    private int currentState = STATE_DEFAULT;
    private boolean iconVisible = false;
    private int iconRes = 0;

    // =====================
    // Constructors
    // =====================

    public RSMenuItem(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public RSMenuItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RSMenuItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // =====================
    // Initialization
    // =====================

    private void init(Context context, AttributeSet attrs) {
        // Layout inflate
        LayoutInflater.from(context).inflate(R.layout.rs_menu_item, this, true);

        // View binding
        rootLayout = findViewById(R.id.rs_menu_item_root);
        iconView = findViewById(R.id.rs_menu_item_icon);
        labelView = findViewById(R.id.rs_menu_item_label);
        checkmarkView = findViewById(R.id.rs_menu_item_checkmark);

        // XML attributes
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RSMenuItem);
            try {
                // Label
                String label = a.getString(R.styleable.RSMenuItem_rsMenuItemLabel);
                if (label != null) {
                    setLabel(label);
                }

                // Icon
                iconRes = a.getResourceId(R.styleable.RSMenuItem_rsMenuItemIcon, 0);
                if (iconRes != 0) {
                    setIcon(iconRes);
                }

                // Icon visibility
                iconVisible = a.getBoolean(R.styleable.RSMenuItem_rsMenuItemIconVisible, false);
                setIconVisible(iconVisible);

                // State
                int state = a.getInt(R.styleable.RSMenuItem_rsMenuItemState, STATE_DEFAULT);
                setState(state);

            } finally {
                a.recycle();
            }
        }
    }

    // =====================
    // Public API - Label
    // =====================

    /**
     * Menü öğesinin label metnini ayarlar.
     *
     * @param label Gösterilecek metin
     */
    public void setLabel(String label) {
        if (labelView != null) {
            labelView.setText(label);
        }
    }

    /**
     * Menü öğesinin label metnini döndürür.
     *
     * @return Label metni
     */
    public String getLabel() {
        return labelView != null ? labelView.getText().toString() : "";
    }

    // =====================
    // Public API - Icon
    // =====================

    /**
     * Menü öğesinin leading ikonunu ayarlar.
     *
     * @param iconRes Drawable resource ID
     */
    public void setIcon(@DrawableRes int iconRes) {
        this.iconRes = iconRes;
        if (iconView != null && iconRes != 0) {
            iconView.setImageResource(iconRes);
        }
    }

    /**
     * Leading ikonun görünürlüğünü ayarlar.
     *
     * @param visible true ise ikon görünür
     */
    public void setIconVisible(boolean visible) {
        this.iconVisible = visible;
        if (iconView != null) {
            iconView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * İkonun görünür olup olmadığını döndürür.
     *
     * @return true ise ikon görünür
     */
    public boolean isIconVisible() {
        return iconVisible;
    }

    // =====================
    // Public API - State
    // =====================

    /**
     * Menü öğesinin durumunu ayarlar.
     *
     * @param state STATE_DEFAULT, STATE_HOVER, STATE_FOCUS, STATE_SELECTED veya
     *              STATE_DISABLED
     */
    public void setState(int state) {
        this.currentState = state;
        applyState();
    }

    /**
     * Mevcut durumu döndürür.
     *
     * @return Durum sabiti
     */
    public int getState() {
        return currentState;
    }

    /**
     * Menü öğesinin seçili olup olmadığını ayarlar.
     *
     * @param selected true ise seçili duruma geçer
     */
    @Override
    public void setSelected(boolean selected) {
        if (selected) {
            setState(STATE_SELECTED);
        } else if (currentState == STATE_SELECTED) {
            setState(STATE_DEFAULT);
        }
        super.setSelected(selected);
    }

    /**
     * Menü öğesinin aktif olup olmadığını ayarlar.
     *
     * @param enabled true ise aktif
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            setState(STATE_DISABLED);
        } else if (currentState == STATE_DISABLED) {
            setState(STATE_DEFAULT);
        }
        super.setEnabled(enabled);
        if (rootLayout != null) {
            rootLayout.setEnabled(enabled);
        }
    }

    private void applyState() {
        if (rootLayout == null || labelView == null || checkmarkView == null) {
            return;
        }

        Context context = getContext();

        switch (currentState) {
            case STATE_DISABLED:
                // Disabled - soluk renkler
                rootLayout.setEnabled(false);
                labelView.setTextColor(ContextCompat.getColor(context, R.color.rs_text_disabled));
                checkmarkView.setVisibility(View.GONE);
                if (iconView != null) {
                    iconView.setAlpha(0.4f);
                }
                break;

            case STATE_SELECTED:
                // Selected - checkmark görünür
                rootLayout.setEnabled(true);
                rootLayout.setSelected(true);
                labelView.setTextColor(ContextCompat.getColor(context, R.color.rs_text_primary));
                checkmarkView.setVisibility(View.VISIBLE);
                resetIconAlpha();
                break;

            case STATE_FOCUS:
                // Focus - 2dp border (manuel background set)
                rootLayout.setEnabled(true);
                rootLayout.setSelected(false);
                rootLayout.setBackgroundResource(R.drawable.rs_menu_item_bg_focus);
                labelView.setTextColor(ContextCompat.getColor(context, R.color.rs_text_primary));
                checkmarkView.setVisibility(View.GONE);
                resetIconAlpha();
                break;

            case STATE_HOVER:
                // Hover - background (handled by selector on press)
                rootLayout.setEnabled(true);
                rootLayout.setSelected(false);
                rootLayout.setPressed(true);
                rootLayout.setBackgroundResource(R.drawable.rs_menu_item_bg);
                labelView.setTextColor(ContextCompat.getColor(context, R.color.rs_text_primary));
                checkmarkView.setVisibility(View.GONE);
                resetIconAlpha();
                break;

            case STATE_DEFAULT:
            default:
                // Default - normal görünüm
                rootLayout.setEnabled(true);
                rootLayout.setSelected(false);
                rootLayout.setPressed(false);
                rootLayout.setBackgroundResource(R.drawable.rs_menu_item_bg);
                labelView.setTextColor(ContextCompat.getColor(context, R.color.rs_text_primary));
                checkmarkView.setVisibility(View.GONE);
                resetIconAlpha();
                break;
        }
    }

    private void resetIconAlpha() {
        if (iconView != null) {
            iconView.setAlpha(1.0f);
        }
    }

    // =====================
    // Click Listener
    // =====================

    /**
     * Root layout'a click listener ekler.
     *
     * @param listener Click listener
     */
    public void setOnMenuItemClickListener(OnClickListener listener) {
        if (rootLayout != null) {
            rootLayout.setOnClickListener(listener);
        }
    }
}
