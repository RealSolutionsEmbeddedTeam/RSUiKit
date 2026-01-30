package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
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
 * RSTooltip (Coachmark) component
 * Supports 12 arrow positions matching Figma design
 */
public class RSTooltip extends FrameLayout {

    // Arrow positions
    public static final int ARROW_TOP_LEFT = 0;
    public static final int ARROW_TOP_CENTER = 1;
    public static final int ARROW_TOP_RIGHT = 2;
    public static final int ARROW_BOTTOM_LEFT = 3;
    public static final int ARROW_BOTTOM_CENTER = 4;
    public static final int ARROW_BOTTOM_RIGHT = 5;
    public static final int ARROW_LEFT_TOP = 6;
    public static final int ARROW_LEFT_CENTER = 7;
    public static final int ARROW_LEFT_BOTTOM = 8;
    public static final int ARROW_RIGHT_TOP = 9;
    public static final int ARROW_RIGHT_CENTER = 10;
    public static final int ARROW_RIGHT_BOTTOM = 11;

    // Action types
    public static final int ACTION_NONE = 0;
    public static final int ACTION_ONE = 1;
    public static final int ACTION_TWO = 2;

    private String title;
    private String message;
    private int arrowPosition = ARROW_TOP_LEFT;
    private int actionType = ACTION_ONE;
    private boolean showClose = true;

    private TextView tvTitle;
    private TextView tvMessage;
    private ImageView btnClose;
    private RSButton btnPrevious;
    private RSButton btnContinue;
    private LinearLayout cardContent;
    private ImageView arrowTop, arrowBottom, arrowLeft, arrowRight;

    public RSTooltip(@NonNull Context context) {
        super(context);
        init(null);
    }

    public RSTooltip(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RSTooltip(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.rs_tooltip, this, true);

        tvTitle = findViewById(R.id.rsTooltipTitle);
        tvMessage = findViewById(R.id.rsTooltipMessage);
        btnClose = findViewById(R.id.rsTooltipClose);
        btnPrevious = findViewById(R.id.rsTooltipBtnPrevious);
        btnContinue = findViewById(R.id.rsTooltipBtnContinue);
        cardContent = findViewById(R.id.rsTooltipCard);

        arrowTop = findViewById(R.id.rsTooltipArrowTop);
        arrowBottom = findViewById(R.id.rsTooltipArrowBottom);
        arrowLeft = findViewById(R.id.rsTooltipArrowLeft);
        arrowRight = findViewById(R.id.rsTooltipArrowRight);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RSTooltip);
            title = a.getString(R.styleable.RSTooltip_rsTooltipTitle);
            message = a.getString(R.styleable.RSTooltip_rsTooltipMessage);
            arrowPosition = a.getInt(R.styleable.RSTooltip_rsTooltipArrowPosition, ARROW_TOP_LEFT);
            actionType = a.getInt(R.styleable.RSTooltip_rsTooltipActionType, ACTION_ONE);
            showClose = a.getBoolean(R.styleable.RSTooltip_rsTooltipShowClose, true);
            a.recycle();
        }

        updateUI();
    }

    private void updateUI() {
        tvTitle.setText(title);
        tvMessage.setText(message);
        btnClose.setVisibility(showClose ? VISIBLE : GONE);

        // Actions
        if (actionType == ACTION_NONE) {
            findViewById(R.id.rsTooltipActions).setVisibility(GONE);
        } else if (actionType == ACTION_ONE) {
            findViewById(R.id.rsTooltipActions).setVisibility(VISIBLE);
            btnPrevious.setVisibility(GONE);
            btnContinue.setVisibility(VISIBLE);
        } else {
            findViewById(R.id.rsTooltipActions).setVisibility(VISIBLE);
            btnPrevious.setVisibility(VISIBLE);
            btnContinue.setVisibility(VISIBLE);
        }

        updateLayout();
    }

    private void updateLayout() {
        if (cardContent == null)
            return;

        ViewGroup.LayoutParams lp = cardContent.getLayoutParams();
        if (!(lp instanceof FrameLayout.LayoutParams)) {
            lp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        FrameLayout.LayoutParams cardParams = (FrameLayout.LayoutParams) lp;

        // Hide all arrows first
        hideAllArrows();

        // Reset margins
        cardParams.setMargins(0, 0, 0, 0);

        // Arrow visual height is 8dp for all directions
        // Top/Bottom containers: 24dp x 8dp
        // Left/Right containers: 24dp x 24dp (to properly contain rotated 24x8 arrow)
        int arrowVisualHeight = dp(8);
        int arrowOffsetH = dp(16); // Horizontal offset for corner arrows
        int arrowOffsetV = dp(16); // Vertical offset for corner arrows

        switch (arrowPosition) {
            // TOP positions - arrow at top, pointing up
            case ARROW_TOP_LEFT:
                showArrow(arrowTop, Gravity.TOP | Gravity.START, arrowOffsetH, 0, 0, 0);
                cardParams.topMargin = arrowVisualHeight;
                break;
            case ARROW_TOP_CENTER:
                showArrow(arrowTop, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0, 0, 0);
                cardParams.topMargin = arrowVisualHeight;
                break;
            case ARROW_TOP_RIGHT:
                showArrow(arrowTop, Gravity.TOP | Gravity.END, 0, 0, arrowOffsetH, 0);
                cardParams.topMargin = arrowVisualHeight;
                break;

            // BOTTOM positions - arrow at bottom, pointing down
            case ARROW_BOTTOM_LEFT:
                showArrow(arrowBottom, Gravity.BOTTOM | Gravity.START, arrowOffsetH, 0, 0, 0);
                cardParams.bottomMargin = arrowVisualHeight;
                break;
            case ARROW_BOTTOM_CENTER:
                showArrow(arrowBottom, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0, 0, 0);
                cardParams.bottomMargin = arrowVisualHeight;
                break;
            case ARROW_BOTTOM_RIGHT:
                showArrow(arrowBottom, Gravity.BOTTOM | Gravity.END, 0, 0, arrowOffsetH, 0);
                cardParams.bottomMargin = arrowVisualHeight;
                break;

            // LEFT positions - arrow at left, pointing left (24x24 container)
            case ARROW_LEFT_TOP:
                showArrow(arrowLeft, Gravity.START | Gravity.TOP, 0, arrowOffsetV, 0, 0);
                cardParams.leftMargin = arrowVisualHeight;
                break;
            case ARROW_LEFT_CENTER:
                showArrow(arrowLeft, Gravity.START | Gravity.CENTER_VERTICAL, 0, 0, 0, 0);
                cardParams.leftMargin = arrowVisualHeight;
                break;
            case ARROW_LEFT_BOTTOM:
                showArrow(arrowLeft, Gravity.START | Gravity.BOTTOM, 0, 0, 0, arrowOffsetV);
                cardParams.leftMargin = arrowVisualHeight;
                break;

            // RIGHT positions - arrow at right, pointing right (24x24 container)
            case ARROW_RIGHT_TOP:
                showArrow(arrowRight, Gravity.END | Gravity.TOP, 0, arrowOffsetV, 0, 0);
                cardParams.rightMargin = arrowVisualHeight;
                break;
            case ARROW_RIGHT_CENTER:
                showArrow(arrowRight, Gravity.END | Gravity.CENTER_VERTICAL, 0, 0, 0, 0);
                cardParams.rightMargin = arrowVisualHeight;
                break;
            case ARROW_RIGHT_BOTTOM:
                showArrow(arrowRight, Gravity.END | Gravity.BOTTOM, 0, 0, 0, arrowOffsetV);
                cardParams.rightMargin = arrowVisualHeight;
                break;
        }

        cardContent.setLayoutParams(cardParams);
    }

    private void hideAllArrows() {
        if (arrowTop != null)
            arrowTop.setVisibility(GONE);
        if (arrowBottom != null)
            arrowBottom.setVisibility(GONE);
        if (arrowLeft != null)
            arrowLeft.setVisibility(GONE);
        if (arrowRight != null)
            arrowRight.setVisibility(GONE);
    }

    private void showArrow(ImageView arrow, int gravity, int l, int t, int r, int b) {
        if (arrow == null)
            return;
        arrow.setVisibility(VISIBLE);
        ViewGroup.LayoutParams lp = arrow.getLayoutParams();
        if (lp instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) lp;
            params.gravity = gravity;
            params.setMargins(l, t, r, b);
            arrow.setLayoutParams(params);
        }
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
        tvTitle.setText(title);
    }

    public void setMessage(String message) {
        this.message = message;
        tvMessage.setText(message);
    }

    public void setArrowPosition(int position) {
        this.arrowPosition = position;
        updateLayout();
    }

    public void setActionType(int type) {
        this.actionType = type;
        updateUI();
    }

    public void setShowClose(boolean show) {
        this.showClose = show;
        btnClose.setVisibility(show ? VISIBLE : GONE);
    }

    public void setOnCloseClickListener(OnClickListener listener) {
        btnClose.setOnClickListener(listener);
    }

    public void setOnContinueClickListener(OnClickListener listener) {
        btnContinue.setOnClickListener(listener);
    }

    public void setOnPreviousClickListener(OnClickListener listener) {
        btnPrevious.setOnClickListener(listener);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
