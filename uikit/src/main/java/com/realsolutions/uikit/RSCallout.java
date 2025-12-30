package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * RealSolutions UI Kit Callout Component
 * <p>
 * Figma tasarımına uygun olarak 16 farklı varyantı destekler:
 * - Type: info, success, warning, error (4)
 * - Title: true/false (2)
 * - Action: true/false (2)
 * <p>
 * Usage (XML):
 * <com.realsolutions.uikit.RSCallout
 * android:layout_width="match_parent"
 * android:layout_height="wrap_content"
 * app:rsCalloutType="info"
 * app:rsCalloutTitle="Title"
 * app:rsCalloutMessage="Banners display an actionable message..."
 * app:rsCalloutActionText="Learn more"
 * app:rsCalloutShowTitle="true"
 * app:rsCalloutShowAction="true" />
 * <p>
 * Usage (Java):
 * RSCallout callout = findViewById(R.id.callout);
 * callout.setType(RSCallout.TYPE_SUCCESS);
 * callout.setTitle("Success!");
 * callout.setMessage("Operation completed.");
 * callout.setActionText("OK");
 * callout.setShowTitle(true);
 * callout.setShowAction(true);
 * callout.setOnActionClickListener(v -> { ... });
 */
public class RSCallout extends FrameLayout {

    // =====================
    // Type Constants (must match attrs.xml)
    // =====================
    public static final int TYPE_INFO = 0;
    public static final int TYPE_SUCCESS = 1;
    public static final int TYPE_WARNING = 2;
    public static final int TYPE_ERROR = 3;

    // =====================
    // Views
    // =====================
    private LinearLayout rootContainer;
    private FrameLayout iconContainer;
    private ImageView iconView;
    private LinearLayout contentContainer;
    private TextView titleView;
    private TextView messageView;
    private TextView actionView;

    // =====================
    // State
    // =====================
    private int type = TYPE_INFO;
    private String title = "";
    private String message = "";
    private String actionText = "";
    private boolean showTitle = false;
    private boolean showAction = false;

    // =====================
    // Listeners
    // =====================
    private OnClickListener actionClickListener;

    // =====================
    // Constructors
    // =====================
    public RSCallout(Context context) {
        super(context);
        init(null);
    }

    public RSCallout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RSCallout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    // =====================
    // Initialization
    // =====================
    private void init(@Nullable AttributeSet attrs) {
        // Layout inflate
        LayoutInflater.from(getContext()).inflate(R.layout.rs_callout, this, true);

        // View binding
        rootContainer = findViewById(R.id.rs_callout_root);
        iconContainer = findViewById(R.id.rs_callout_icon_container);
        iconView = findViewById(R.id.rs_callout_icon);
        contentContainer = findViewById(R.id.rs_callout_content);
        titleView = findViewById(R.id.rs_callout_title);
        messageView = findViewById(R.id.rs_callout_message);
        actionView = findViewById(R.id.rs_callout_action);

        // Read attrs from XML
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RSCallout);

            type = a.getInt(R.styleable.RSCallout_rsCalloutType, TYPE_INFO);
            title = a.getString(R.styleable.RSCallout_rsCalloutTitle);
            message = a.getString(R.styleable.RSCallout_rsCalloutMessage);
            actionText = a.getString(R.styleable.RSCallout_rsCalloutActionText);
            showTitle = a.getBoolean(R.styleable.RSCallout_rsCalloutShowTitle, false);
            showAction = a.getBoolean(R.styleable.RSCallout_rsCalloutShowAction, false);

            // Handle null strings
            if (title == null)
                title = "";
            if (message == null)
                message = "";
            if (actionText == null)
                actionText = "";

            a.recycle();
        }

        // Apply initial state
        applyType();
        updateViews();

        // Action click listener
        actionView.setOnClickListener(v -> {
            if (actionClickListener != null) {
                actionClickListener.onClick(v);
            }
        });
    }

    // =====================
    // Type Application (Appearance)
    // =====================
    private void applyType() {
        int bgResId;
        int iconResId;
        int iconBgResId;

        // Figma specs: Title ve Message renkleri tüm tiplerde sabit
        // Title: rs-text-primary
        // Message: rs-text-tertiary
        // Action: rs-text-primary

        switch (type) {
            case TYPE_SUCCESS:
                bgResId = R.drawable.rs_callout_bg_success;
                iconResId = R.drawable.rs_ic_callout_success;
                iconBgResId = R.drawable.rs_callout_icon_bg_success;
                break;

            case TYPE_WARNING:
                bgResId = R.drawable.rs_callout_bg_warning;
                iconResId = R.drawable.rs_ic_callout_warning;
                iconBgResId = R.drawable.rs_callout_icon_bg_warning;
                break;

            case TYPE_ERROR:
                bgResId = R.drawable.rs_callout_bg_error;
                iconResId = R.drawable.rs_ic_callout_error;
                iconBgResId = R.drawable.rs_callout_icon_bg_error;
                break;

            case TYPE_INFO:
            default:
                bgResId = R.drawable.rs_callout_bg_info;
                iconResId = R.drawable.rs_ic_callout_info;
                iconBgResId = R.drawable.rs_callout_icon_bg_info;
                break;
        }

        // Apply container background
        rootContainer.setBackground(ContextCompat.getDrawable(getContext(), bgResId));

        // Apply icon container background (28x28 circle)
        iconContainer.setBackground(ContextCompat.getDrawable(getContext(), iconBgResId));

        // Apply icon
        iconView.setImageResource(iconResId);
        iconView.clearColorFilter();

        // Text colors are set in XML layout (rs-text-primary, rs-text-tertiary)
        // No need to set them here as they are consistent across all types

        invalidate();
        requestLayout();
    }

    // =====================
    // View Updates
    // =====================
    private void updateViews() {
        // Title
        titleView.setText(title);
        titleView.setVisibility(showTitle && !title.isEmpty() ? View.VISIBLE : View.GONE);

        // Message
        messageView.setText(message);
        messageView.setVisibility(!message.isEmpty() ? View.VISIBLE : View.GONE);

        // Action
        actionView.setText(actionText);
        actionView.setVisibility(showAction && !actionText.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // =====================
    // Public API - Setters
    // =====================

    /**
     * Set the callout type (info, success, warning, error)
     */
    public void setType(int type) {
        if (type < TYPE_INFO || type > TYPE_ERROR) {
            type = TYPE_INFO;
        }
        this.type = type;
        applyType();
    }

    /**
     * Set the title text
     */
    public void setTitle(String title) {
        this.title = title != null ? title : "";
        updateViews();
    }

    /**
     * Set the message text
     */
    public void setMessage(String message) {
        this.message = message != null ? message : "";
        updateViews();
    }

    /**
     * Set the action button text
     */
    public void setActionText(String actionText) {
        this.actionText = actionText != null ? actionText : "";
        updateViews();
    }

    /**
     * Show or hide the title
     */
    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
        updateViews();
    }

    /**
     * Show or hide the action button
     */
    public void setShowAction(boolean showAction) {
        this.showAction = showAction;
        updateViews();
    }

    /**
     * Set click listener for the action button
     */
    public void setOnActionClickListener(OnClickListener listener) {
        this.actionClickListener = listener;
    }

    /**
     * Set a custom icon drawable
     */
    public void setIcon(@DrawableRes int iconResId) {
        iconView.setImageResource(iconResId);
    }

    /**
     * Set a custom icon drawable
     */
    public void setIcon(Drawable drawable) {
        iconView.setImageDrawable(drawable);
    }

    // =====================
    // Public API - Getters
    // =====================

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getActionText() {
        return actionText;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public boolean isShowAction() {
        return showAction;
    }

    // =====================
    // Utility
    // =====================
    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }
}
