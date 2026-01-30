package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * RSRadioCard - RealSolutions UI Kit Radio Card Component
 * <p>
 * A selectable card with radio indicator, label, description, and helper text.
 * <p>
 * Figma Specs:
 * - Width: 400dp (fixed or match_parent)
 * - Height: Hug content (92dp min)
 * - Radius: rs-radius-xl (16dp)
 * - Border: 1dp inside
 * - Padding: 16dp
 * - Gap: 8dp between radio and text content
 * <p>
 * Attributes:
 * - rsRadioCardLabel: Label text
 * - rsRadioCardDescription: Description text
 * - rsRadioCardHelper: Helper text
 * - rsRadioCardShowDescription: Show/hide description
 * - rsRadioCardShowHelper: Show/hide helper
 * - rsRadioCardSelected: Selected state (Status in Figma)
 * - rsSize: sm (16dp) or md (20dp)
 * - rsRadioCardState: default, hover, focus, pressed (State in Figma)
 * <p>
 * Usage (XML):
 *
 * <pre>
 * &lt;com.realsolutions.uikit.RSRadioCard
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content"
 *     app:rsRadioCardLabel="Label (Optional)"
 *     app:rsRadioCardDescription="Lorem ipsum dolor sit amet..."
 *     app:rsRadioCardShowDescription="true"
 *     app:rsRadioCardSelected="false"
 *     app:rsSize="sm"
 *     app:rsRadioCardState="default_state" /&gt;
 * </pre>
 */
public class RSRadioCard extends FrameLayout {

    // =====================
    // Size Constants (matches Figma)
    // =====================
    public static final int SIZE_SM = 1; // 16dp radio indicator
    public static final int SIZE_MD = 2; // 20dp radio indicator

    // =====================
    // State Constants (matches Figma)
    // =====================
    public static final int STATE_DEFAULT = 0;
    public static final int STATE_HOVER = 1;
    public static final int STATE_FOCUS = 2;
    public static final int STATE_PRESSED = 3;

    // Size values in dp
    private static final int RADIO_SIZE_SM = 16;
    private static final int RADIO_SIZE_MD = 20;
    private static final int RADIO_DOT_SIZE_SM = 8;
    private static final int RADIO_DOT_SIZE_MD = 10;

    // =====================
    // Views
    // =====================
    private View rootView;
    private FrameLayout radioIndicator;
    private View radioOuter;
    private View radioInner;
    private TextView labelView;
    private TextView descriptionView;
    private TextView helperView;
    private View accentBar;

    // =====================
    // State
    // =====================
    private String label = "";
    private String description = "";
    private String helper = "";
    private boolean showDescription = true;
    private boolean showHelper = false;
    private boolean selected = false;
    private boolean cardEnabled = true;
    private int size = SIZE_SM;
    private int visualState = STATE_DEFAULT;

    // =====================
    // Listener
    // =====================
    private OnSelectionChangedListener selectionListener;

    public interface OnSelectionChangedListener {
        void onSelectionChanged(RSRadioCard card, boolean isSelected);
    }

    // =====================
    // Constructors
    // =====================

    public RSRadioCard(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public RSRadioCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RSRadioCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // =====================
    // Initialization
    // =====================

    private void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.rs_radio_card, this, true);

        // Find views
        rootView = findViewById(R.id.rsRadioCardRoot);
        radioIndicator = findViewById(R.id.rsRadioIndicator);
        radioOuter = findViewById(R.id.rsRadioOuter);
        radioInner = findViewById(R.id.rsRadioInner);
        labelView = findViewById(R.id.rsRadioCardLabel);
        descriptionView = findViewById(R.id.rsRadioCardDescription);
        helperView = findViewById(R.id.rsRadioCardHelper);
        accentBar = findViewById(R.id.rsRadioCardAccent);

        // Read attributes
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RSRadioCard);
            try {
                label = a.getString(R.styleable.RSRadioCard_rsRadioCardLabel);
                if (label == null)
                    label = "";

                description = a.getString(R.styleable.RSRadioCard_rsRadioCardDescription);
                if (description == null)
                    description = "";

                helper = a.getString(R.styleable.RSRadioCard_rsRadioCardHelper);
                if (helper == null)
                    helper = "";

                showDescription = a.getBoolean(R.styleable.RSRadioCard_rsRadioCardShowDescription, true);
                showHelper = a.getBoolean(R.styleable.RSRadioCard_rsRadioCardShowHelper, false);
                selected = a.getBoolean(R.styleable.RSRadioCard_rsRadioCardSelected, false);
                cardEnabled = a.getBoolean(R.styleable.RSRadioCard_rsRadioCardEnabled, true);
                size = a.getInt(R.styleable.RSRadioCard_rsSize, SIZE_SM);
                visualState = a.getInt(R.styleable.RSRadioCard_rsRadioCardState, STATE_DEFAULT);
            } finally {
                a.recycle();
            }
        }

        // Setup click listener
        setOnClickListener(v -> {
            if (cardEnabled) {
                setSelected(!selected);
                if (selectionListener != null) {
                    selectionListener.onSelectionChanged(this, selected);
                }
            }
        });

        updateViews();
    }

    // =====================
    // View Updates
    // =====================

    private void updateViews() {
        // Label
        labelView.setText(label);

        // Description
        if (showDescription && !description.isEmpty()) {
            descriptionView.setText(description);
            descriptionView.setVisibility(VISIBLE);
        } else {
            descriptionView.setVisibility(GONE);
        }

        // Helper
        if (showHelper && !helper.isEmpty()) {
            helperView.setText(helper);
            helperView.setVisibility(VISIBLE);
        } else {
            helperView.setVisibility(GONE);
        }

        // Size
        applySize();

        // Selection state
        updateSelectionState();

        // Enabled state
        setAlpha(cardEnabled ? 1.0f : 0.5f);

        // Visual state
        applyVisualState();
    }

    private void applySize() {
        float density = getResources().getDisplayMetrics().density;

        int radioSize = (size == SIZE_MD) ? RADIO_SIZE_MD : RADIO_SIZE_SM;
        int dotSize = (size == SIZE_MD) ? RADIO_DOT_SIZE_MD : RADIO_DOT_SIZE_SM;

        int radioSizePx = (int) (radioSize * density);
        int dotSizePx = (int) (dotSize * density);

        // Update radio indicator size
        ViewGroup.LayoutParams indicatorParams = radioIndicator.getLayoutParams();
        indicatorParams.width = radioSizePx;
        indicatorParams.height = radioSizePx;
        radioIndicator.setLayoutParams(indicatorParams);

        // Update inner dot size
        FrameLayout.LayoutParams dotParams = (FrameLayout.LayoutParams) radioInner.getLayoutParams();
        dotParams.width = dotSizePx;
        dotParams.height = dotSizePx;
        radioInner.setLayoutParams(dotParams);
    }

    private void updateSelectionState() {
        // Activate state for selector drawables
        rootView.setActivated(selected);
        radioOuter.setActivated(selected);

        // Inner dot visibility
        radioInner.setVisibility(selected ? VISIBLE : GONE);

        // Left accent bar
        accentBar.setVisibility(selected ? VISIBLE : GONE);
    }

    private void applyVisualState() {
        // Apply visual state for preview/demo
        switch (visualState) {
            case STATE_HOVER:
                rootView.setHovered(true);
                rootView.setPressed(false);
                break;
            case STATE_FOCUS:
                rootView.requestFocus();
                break;
            case STATE_PRESSED:
                rootView.setPressed(true);
                break;
            case STATE_DEFAULT:
            default:
                rootView.setHovered(false);
                rootView.setPressed(false);
                break;
        }
    }

    // =====================
    // Public API
    // =====================

    /**
     * Set the label text.
     */
    public void setLabel(String label) {
        this.label = label != null ? label : "";
        labelView.setText(this.label);
    }

    /**
     * Get the label text.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the description text.
     */
    public void setDescription(String description) {
        this.description = description != null ? description : "";
        if (showDescription && !this.description.isEmpty()) {
            descriptionView.setText(this.description);
            descriptionView.setVisibility(VISIBLE);
        } else {
            descriptionView.setVisibility(GONE);
        }
    }

    /**
     * Get the description text.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the helper text.
     */
    public void setHelper(String helper) {
        this.helper = helper != null ? helper : "";
        if (showHelper && !this.helper.isEmpty()) {
            helperView.setText(this.helper);
            helperView.setVisibility(VISIBLE);
        } else {
            helperView.setVisibility(GONE);
        }
    }

    /**
     * Set description visibility.
     */
    public void setShowDescription(boolean show) {
        this.showDescription = show;
        if (show && !description.isEmpty()) {
            descriptionView.setVisibility(VISIBLE);
        } else {
            descriptionView.setVisibility(GONE);
        }
    }

    /**
     * Set helper visibility.
     */
    public void setShowHelper(boolean show) {
        this.showHelper = show;
        if (show && !helper.isEmpty()) {
            helperView.setVisibility(VISIBLE);
        } else {
            helperView.setVisibility(GONE);
        }
    }

    /**
     * Set the selected state (Status in Figma).
     */
    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        updateSelectionState();
    }

    /**
     * Get the selected state.
     */
    @Override
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set the enabled state.
     */
    public void setCardEnabled(boolean enabled) {
        this.cardEnabled = enabled;
        setAlpha(enabled ? 1.0f : 0.5f);
        setClickable(enabled);
    }

    /**
     * Check if card is enabled.
     */
    public boolean isCardEnabled() {
        return cardEnabled;
    }

    /**
     * Set the size (SIZE_SM or SIZE_MD).
     */
    public void setSize(int size) {
        this.size = size;
        applySize();
    }

    /**
     * Get the current size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Set visual state (for preview/demo).
     *
     * @param state STATE_DEFAULT, STATE_HOVER, STATE_FOCUS, or STATE_PRESSED
     */
    public void setVisualState(int state) {
        this.visualState = state;
        applyVisualState();
    }

    /**
     * Get current visual state.
     */
    public int getVisualState() {
        return visualState;
    }

    /**
     * Set selection change listener.
     */
    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionListener = listener;
    }
}
