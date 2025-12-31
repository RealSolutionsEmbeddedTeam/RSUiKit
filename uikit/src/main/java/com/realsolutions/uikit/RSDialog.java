package com.realsolutions.uikit;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * RSDialog - RealSolutions UI Kit Dialog Component
 * <p>
 * Figma tasarımına uygun, özelleştirilebilir dialog bileşeni.
 * 4 farklı varyantı destekler:
 * - Icon gösterimli / gizli
 * - Tek buton (Primary) / İki buton (Neutral + Primary)
 * - Animasyonlu ikon (opsiyonel rotation)
 * <p>
 * Kullanım (XML):
 * <com.realsolutions.uikit.RSDialog
 * android:layout_width="wrap_content"
 * android:layout_height="wrap_content"
 * app:rsDialogShowIcon="true"
 * app:rsDialogAnimateIcon="true"
 * app:rsDialogActionType="defaultAction"
 * app:rsDialogTitle="Location Services"
 * app:rsDialogDescription="Turn on GPS location to see more relevant weather
 * reports."
 * app:rsDialogNeutralText="Neutral"
 * app:rsDialogPrimaryText="Primary"
 * app:rsDialogIcon="@drawable/rs_ic_dialog_location" />
 */
public class RSDialog extends FrameLayout {

    // Action type constants
    public static final int ACTION_DEFAULT = 0; // 2 buttons (Neutral + Primary)
    public static final int ACTION_SINGLE = 1; // 1 button (Primary only)

    // Child views
    private FrameLayout iconContainer;
    private ImageView iconView;
    private TextView titleView;
    private TextView descriptionView;
    private LinearLayout buttonsContainer;
    private RSButton neutralButton;
    private RSButton primaryButton;

    // State
    private boolean showIcon = true;
    private boolean animateIcon = false;
    private int actionType = ACTION_DEFAULT;

    // Animation
    private ObjectAnimator iconRotationAnimator;

    public RSDialog(@NonNull Context context) {
        super(context);
        init(null);
    }

    public RSDialog(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RSDialog(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        // Inflate layout
        LayoutInflater.from(getContext()).inflate(R.layout.rs_dialog, this, true);

        // Find views
        iconContainer = findViewById(R.id.rs_dialog_icon_container);
        iconView = findViewById(R.id.rs_dialog_icon);
        titleView = findViewById(R.id.rs_dialog_title);
        descriptionView = findViewById(R.id.rs_dialog_description);
        buttonsContainer = findViewById(R.id.rs_dialog_buttons_container);
        neutralButton = findViewById(R.id.rs_dialog_neutral_button);
        primaryButton = findViewById(R.id.rs_dialog_primary_button);

        // Read XML attributes
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RSDialog);

            showIcon = a.getBoolean(R.styleable.RSDialog_rsDialogShowIcon, true);
            animateIcon = a.getBoolean(R.styleable.RSDialog_rsDialogAnimateIcon, false);
            actionType = a.getInt(R.styleable.RSDialog_rsDialogActionType, ACTION_DEFAULT);

            String title = a.getString(R.styleable.RSDialog_rsDialogTitle);
            String description = a.getString(R.styleable.RSDialog_rsDialogDescription);
            String neutralText = a.getString(R.styleable.RSDialog_rsDialogNeutralText);
            String primaryText = a.getString(R.styleable.RSDialog_rsDialogPrimaryText);
            int iconRes = a.getResourceId(R.styleable.RSDialog_rsDialogIcon, 0);

            // Button types - only apply if explicitly set in XML
            // Otherwise, use the default types from rs_dialog.xml layout (neutral +
            // primary)
            if (a.hasValue(R.styleable.RSDialog_rsDialogNeutralButtonType)) {
                int neutralButtonType = a.getInt(R.styleable.RSDialog_rsDialogNeutralButtonType, RSButton.TYPE_NEUTRAL);
                neutralButton.setType(neutralButtonType);
            }
            if (a.hasValue(R.styleable.RSDialog_rsDialogPrimaryButtonType)) {
                int primaryButtonType = a.getInt(R.styleable.RSDialog_rsDialogPrimaryButtonType, RSButton.TYPE_PRIMARY);
                primaryButton.setType(primaryButtonType);
            }

            a.recycle();

            // Apply values
            if (title != null) {
                titleView.setText(title);
            }
            if (description != null) {
                descriptionView.setText(description);
            }
            if (neutralText != null) {
                neutralButton.setText(neutralText);
            }
            if (primaryText != null) {
                primaryButton.setText(primaryText);
            }
            if (iconRes != 0) {
                iconView.setImageResource(iconRes);
            } else {
                // Default icon
                iconView.setImageResource(R.drawable.rs_ic_info);
            }
        } else {
            // Default icon
            iconView.setImageResource(R.drawable.rs_ic_info);
        }

        // Apply visibility states
        updateIconVisibility();
        updateActionType();

        // Start animation if enabled
        if (animateIcon) {
            startIconAnimation();
        }

        // Set elevation for shadow effect
        setElevation(dp(8));
    }

    /**
     * Show or hide the icon container.
     */
    public void setShowIcon(boolean show) {
        this.showIcon = show;
        updateIconVisibility();
    }

    /**
     * Get current icon visibility state.
     */
    public boolean isShowIcon() {
        return showIcon;
    }

    /**
     * Enable or disable icon rotation animation.
     */
    public void setAnimateIcon(boolean animate) {
        this.animateIcon = animate;
        if (animate) {
            startIconAnimation();
        } else {
            stopIconAnimation();
        }
    }

    /**
     * Check if icon animation is enabled.
     */
    public boolean isAnimateIcon() {
        return animateIcon;
    }

    /**
     * Start the icon rotation animation.
     */
    public void startIconAnimation() {
        if (iconRotationAnimator != null && iconRotationAnimator.isRunning()) {
            return; // Already running
        }

        iconRotationAnimator = ObjectAnimator.ofFloat(iconView, "rotation", 0f, 360f);
        iconRotationAnimator.setDuration(2000); // 2 seconds per rotation
        iconRotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        iconRotationAnimator.setRepeatMode(ValueAnimator.RESTART);
        iconRotationAnimator.setInterpolator(new LinearInterpolator());
        iconRotationAnimator.start();
    }

    /**
     * Stop the icon rotation animation.
     */
    public void stopIconAnimation() {
        if (iconRotationAnimator != null) {
            iconRotationAnimator.cancel();
            iconView.setRotation(0f); // Reset rotation
        }
    }

    /**
     * Set action type (single or default with two buttons).
     *
     * @param type ACTION_DEFAULT (2 buttons) or ACTION_SINGLE (1 button)
     */
    public void setActionType(int type) {
        this.actionType = type;
        updateActionType();
    }

    /**
     * Get current action type.
     */
    public int getActionType() {
        return actionType;
    }

    /**
     * Set dialog title.
     */
    public void setTitle(String title) {
        titleView.setText(title);
    }

    /**
     * Set dialog description.
     */
    public void setDescription(String description) {
        descriptionView.setText(description);
    }

    /**
     * Set neutral button text.
     */
    public void setNeutralButtonText(String text) {
        neutralButton.setText(text);
    }

    /**
     * Set primary button text.
     */
    public void setPrimaryButtonText(String text) {
        primaryButton.setText(text);
    }

    /**
     * Set neutral button type.
     *
     * @param type RSButton.TYPE_PRIMARY, TYPE_SECONDARY, TYPE_NEUTRAL,
     *             TYPE_PLAIN_DARK, or TYPE_PLAIN_LIGHT
     */
    public void setNeutralButtonType(int type) {
        neutralButton.setType(type);
    }

    /**
     * Set primary button type.
     *
     * @param type RSButton.TYPE_PRIMARY, TYPE_SECONDARY, TYPE_NEUTRAL,
     *             TYPE_PLAIN_DARK, or TYPE_PLAIN_LIGHT
     */
    public void setPrimaryButtonType(int type) {
        primaryButton.setType(type);
    }

    /**
     * Set click listener for neutral button.
     */
    public void setOnNeutralClickListener(OnClickListener listener) {
        neutralButton.setOnClickListener(listener);
    }

    /**
     * Set click listener for primary button.
     */
    public void setOnPrimaryClickListener(OnClickListener listener) {
        primaryButton.setOnClickListener(listener);
    }

    /**
     * Set custom icon.
     */
    public void setIcon(@DrawableRes int resId) {
        iconView.setImageResource(resId);
    }

    /**
     * Get title TextView for advanced customization.
     */
    public TextView getTitleView() {
        return titleView;
    }

    /**
     * Get description TextView for advanced customization.
     */
    public TextView getDescriptionView() {
        return descriptionView;
    }

    /**
     * Get neutral button for advanced customization.
     */
    public RSButton getNeutralButton() {
        return neutralButton;
    }

    /**
     * Get primary button for advanced customization.
     */
    public RSButton getPrimaryButton() {
        return primaryButton;
    }

    /**
     * Get icon ImageView for advanced customization.
     */
    public ImageView getIconView() {
        return iconView;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Stop animation when view is detached to prevent memory leaks
        stopIconAnimation();
    }

    // ---------- Private helpers ----------

    private void updateIconVisibility() {
        iconContainer.setVisibility(showIcon ? View.VISIBLE : View.GONE);
        if (!showIcon) {
            stopIconAnimation();
        }
    }

    private void updateActionType() {
        if (actionType == ACTION_SINGLE) {
            // Single button mode - hide neutral, make primary fill width
            neutralButton.setVisibility(View.GONE);

            // Remove margin from primary button
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) primaryButton.getLayoutParams();
            params.setMarginStart(0);
            primaryButton.setLayoutParams(params);
        } else {
            // Default mode - show both buttons
            neutralButton.setVisibility(View.VISIBLE);

            // Restore margin
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) primaryButton.getLayoutParams();
            params.setMarginStart(0);
            primaryButton.setLayoutParams(params);
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
