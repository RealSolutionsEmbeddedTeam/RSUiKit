package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * RSSnackbar - Bildirim mesajları için snackbar bileşeni.
 * <p>
 * Figma Tasarım Özellikleri:
 * - Padding: 12dp
 * - Border-radius: 16dp
 * - Gap: 8dp
 * - Icon size: 28dp
 * - Background: rs_bg_overlay
 * <p>
 * Type'lar:
 * - INFORMATIVE: Turuncu ikon (bilgi)
 * - SUCCESS: Yeşil ikon (başarı)
 * - CRITICAL: Kırmızı ikon (hata)
 * <p>
 * Kullanım (XML):
 *
 * <pre>
 * &lt;com.realsolutions.uikit.RSSnackbar
 *     app:rsSnackbarType="success"
 *     app:rsSnackbarMessage="İşlem başarılı!"
 *     app:rsSnackbarActionText="Geri Al"
 *     app:rsSnackbarShowAction="true" /&gt;
 * </pre>
 * <p>
 * Kullanım (Java):
 *
 * <pre>
 * RSSnackbar snackbar = new RSSnackbar(context);
 * snackbar.setType(RSSnackbar.TYPE_SUCCESS);
 * snackbar.setMessage("İşlem başarılı!");
 * snackbar.setActionText("Geri Al");
 * snackbar.setShowAction(true);
 * </pre>
 */
public class RSSnackbar extends FrameLayout {

    // =====================
    // Type Constants
    // =====================

    /**
     * Bilgilendirme tipi - turuncu ikon
     */
    public static final int TYPE_INFORMATIVE = 0;
    /**
     * Başarı tipi - yeşil ikon
     */
    public static final int TYPE_SUCCESS = 1;
    /**
     * Kritik/hata tipi - kırmızı ikon
     */
    public static final int TYPE_CRITICAL = 2;

    // =====================
    // Views
    // =====================

    private LinearLayout rootLayout;
    private FrameLayout iconContainer;
    private ImageView iconView;
    private TextView messageView;
    private RSButton actionButton;

    // =====================
    // State
    // =====================

    private int currentType = TYPE_INFORMATIVE;
    private boolean showAction = false;

    // =====================
    // Constructors
    // =====================

    public RSSnackbar(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public RSSnackbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RSSnackbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // =====================
    // Initialization
    // =====================

    private void init(Context context, AttributeSet attrs) {
        // Layout inflate
        LayoutInflater.from(context).inflate(R.layout.rs_snackbar, this, true);

        // View binding
        rootLayout = findViewById(R.id.rs_snackbar_root);
        iconContainer = findViewById(R.id.rs_snackbar_icon_container);
        iconView = findViewById(R.id.rs_snackbar_icon);
        messageView = findViewById(R.id.rs_snackbar_message);
        actionButton = findViewById(R.id.rs_snackbar_action);

        // XML attributes
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RSSnackbar);
            try {
                // Type
                int type = a.getInt(R.styleable.RSSnackbar_rsSnackbarType, TYPE_INFORMATIVE);
                setType(type);

                // Message
                String message = a.getString(R.styleable.RSSnackbar_rsSnackbarMessage);
                if (message != null) {
                    setMessage(message);
                }

                // Action text
                String actionText = a.getString(R.styleable.RSSnackbar_rsSnackbarActionText);
                if (actionText != null) {
                    setActionText(actionText);
                }

                // Show action
                showAction = a.getBoolean(R.styleable.RSSnackbar_rsSnackbarShowAction, false);
                setShowAction(showAction);

            } finally {
                a.recycle();
            }
        }
    }

    // =====================
    // Public API - Type
    // =====================

    /**
     * Snackbar tipini ayarlar.
     *
     * @param type TYPE_INFORMATIVE, TYPE_SUCCESS veya TYPE_CRITICAL
     */
    public void setType(int type) {
        this.currentType = type;
        applyType();
    }

    /**
     * Mevcut tipi döndürür.
     *
     * @return Tip sabiti
     */
    public int getType() {
        return currentType;
    }

    private void applyType() {
        if (iconView == null || iconContainer == null)
            return;

        switch (currentType) {
            case TYPE_SUCCESS:
                iconContainer.setBackgroundResource(R.drawable.rs_snackbar_icon_bg_success);
                iconView.setImageResource(R.drawable.rs_ic_snackbar_success);
                break;

            case TYPE_CRITICAL:
                iconContainer.setBackgroundResource(R.drawable.rs_snackbar_icon_bg_error);
                iconView.setImageResource(R.drawable.rs_ic_snackbar_error);
                break;

            case TYPE_INFORMATIVE:
            default:
                iconContainer.setBackgroundResource(R.drawable.rs_snackbar_icon_bg_warning);
                iconView.setImageResource(R.drawable.rs_ic_snackbar_info);
                break;
        }
    }

    // =====================
    // Public API - Message
    // =====================

    /**
     * Snackbar mesajını ayarlar.
     *
     * @param message Gösterilecek mesaj
     */
    public void setMessage(String message) {
        if (messageView != null) {
            messageView.setText(message);
        }
    }

    /**
     * Mevcut mesajı döndürür.
     *
     * @return Mesaj metni
     */
    public String getMessage() {
        return messageView != null ? messageView.getText().toString() : "";
    }

    // =====================
    // Public API - Action
    // =====================

    /**
     * Action button metnini ayarlar.
     *
     * @param text Button metni
     */
    public void setActionText(String text) {
        if (actionButton != null) {
            actionButton.setText(text);
        }
    }

    /**
     * Action button görünürlüğünü ayarlar.
     *
     * @param show true ise button görünür
     */
    public void setShowAction(boolean show) {
        this.showAction = show;
        if (actionButton != null) {
            actionButton.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Action button'a click listener ekler.
     *
     * @param listener Click listener
     */
    public void setOnActionClickListener(OnClickListener listener) {
        if (actionButton != null) {
            actionButton.setOnClickListener(listener);
        }
    }

    // =====================
    // Show/Dismiss
    // =====================

    /**
     * Snackbar'ı belirtilen parent'a ekler.
     *
     * @param parent Eklenecek ViewGroup
     */
    public void show(ViewGroup parent) {
        if (parent != null && getParent() == null) {
            parent.addView(this);
        }
    }

    /**
     * Snackbar'ı parent'tan kaldırır.
     */
    public void dismiss() {
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            parent.removeView(this);
        }
    }
}
