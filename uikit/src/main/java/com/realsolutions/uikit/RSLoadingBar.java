package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * RSLoadingBar - RealSolutions UI Kit Loading/Progress Bar Component
 * <p>
 * A horizontal progress bar component that matches the Figma design.
 * Progress level ranges from 0 to 100.
 * <p>
 * XML Usage:
 * <com.realsolutions.uikit.RSLoadingBar
 * android:layout_width="328dp"
 * android:layout_height="4dp"
 * app:rsLevel="50" />
 * <p>
 * Java Usage:
 * loadingBar.setProgressLevel(75);
 * loadingBar.animateToLevel(100, 1000);
 * <p>
 * Figma Specifications:
 * - Width: 328dp
 * - Height: 4dp
 * - Corner Radius: 50dp (fully rounded)
 * - Track Color: rs_bg_quanternary (#E9EAEB)
 * - Progress Color: rs_bg_brand_solid (#3E4BD9)
 */
public class RSLoadingBar extends View {

    // ==================== CONSTANTS ====================

    /**
     * Default progress level
     */
    private static final int DEFAULT_PROGRESS_LEVEL = 0;

    /**
     * Minimum progress level
     */
    private static final int MIN_PROGRESS_LEVEL = 0;

    /**
     * Maximum progress level
     */
    private static final int MAX_PROGRESS_LEVEL = 100;

    /**
     * Animation frame interval in milliseconds (~60 FPS)
     */
    private static final int ANIMATION_FRAME_INTERVAL_MS = 16;

    // ==================== STATE VARIABLES ====================

    /**
     * Current progress level (0-100)
     */
    private int currentProgressLevel = DEFAULT_PROGRESS_LEVEL;

    // ==================== DRAWING OBJECTS ====================

    /**
     * Paint object for the track background (gray color)
     */
    private Paint trackBackgroundPaint;

    /**
     * Paint object for the progress indicator (blue color)
     */
    private Paint progressIndicatorPaint;

    /**
     * Rectangle bounds for the track background (full width)
     */
    private RectF trackBackgroundBounds;

    /**
     * Rectangle bounds for the progress indicator (based on level)
     */
    private RectF progressIndicatorBounds;

    /**
     * Corner radius in pixels for rounded corners
     */
    private float cornerRadiusInPixels;

    // ==================== CONSTRUCTORS ====================

    public RSLoadingBar(@NonNull Context context) {
        super(context);
        initializeComponent(null);
    }

    public RSLoadingBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeComponent(attrs);
    }

    public RSLoadingBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeComponent(attrs);
    }

    // ==================== INITIALIZATION ====================

    /**
     * Initializes the component with default values and XML attributes.
     * Sets up paint objects, rectangles, and reads XML properties.
     */
    private void initializeComponent(@Nullable AttributeSet xmlAttributes) {
        // Corner radius: 50dp (fully rounded appearance in Figma)
        cornerRadiusInPixels = convertDpToPixels(50);

        // Track background paint: Gray color (rs_bg_quanternary - #E9EAEB)
        trackBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackBackgroundPaint.setStyle(Paint.Style.FILL);
        trackBackgroundPaint.setColor(ContextCompat.getColor(getContext(), R.color.rs_gray_light_200));

        // Progress indicator paint: Blue color (rs_bg_brand_solid - #3E4BD9)
        progressIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressIndicatorPaint.setStyle(Paint.Style.FILL);
        progressIndicatorPaint.setColor(ContextCompat.getColor(getContext(), R.color.rs_brand_600));

        // Initialize rectangle bounds
        trackBackgroundBounds = new RectF();
        progressIndicatorBounds = new RectF();

        // Read XML attributes
        if (xmlAttributes != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(
                    xmlAttributes, R.styleable.RSLoadingBar);

            currentProgressLevel = typedArray.getInt(
                    R.styleable.RSLoadingBar_rsLevel, DEFAULT_PROGRESS_LEVEL);
            currentProgressLevel = clampValue(currentProgressLevel, MIN_PROGRESS_LEVEL, MAX_PROGRESS_LEVEL);

            typedArray.recycle();
        }
    }

    // ==================== VIEW LIFECYCLE ====================

    @Override
    protected void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
        updateDrawingBounds();
    }

    /**
     * Updates the track and progress rectangle bounds.
     * Called when view size changes or progress level changes.
     */
    private void updateDrawingBounds() {
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        // Track background: covers full width
        trackBackgroundBounds.set(0, 0, viewWidth, viewHeight);

        // Progress indicator: width calculated based on percentage
        float progressWidth = (viewWidth * currentProgressLevel) / 100f;
        progressIndicatorBounds.set(0, 0, progressWidth, viewHeight);
    }

    // ==================== DRAWING ====================

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // 1. Draw track background (gray - always visible)
        canvas.drawRoundRect(trackBackgroundBounds, cornerRadiusInPixels,
                cornerRadiusInPixels, trackBackgroundPaint);

        // 2. Draw progress indicator (blue - only if level > 0)
        if (currentProgressLevel > 0) {
            canvas.drawRoundRect(progressIndicatorBounds, cornerRadiusInPixels,
                    cornerRadiusInPixels, progressIndicatorPaint);
        }
    }

    // ==================== PUBLIC API ====================

    /**
     * Sets the progress level (0-100).
     *
     * @param newLevel New progress value (0: empty, 100: full)
     */
    public void setProgressLevel(int newLevel) {
        this.currentProgressLevel = clampValue(newLevel, MIN_PROGRESS_LEVEL, MAX_PROGRESS_LEVEL);
        updateDrawingBounds();
        invalidate(); // Trigger redraw
    }

    /**
     * Gets the current progress level.
     *
     * @return Current level (0-100)
     */
    public int getProgressLevel() {
        return currentProgressLevel;
    }

    /**
     * Alias for setProgressLevel (backward compatibility).
     */
    public void setLevel(int level) {
        setProgressLevel(level);
    }

    /**
     * Alias for getProgressLevel (backward compatibility).
     */
    public int getLevel() {
        return getProgressLevel();
    }

    /**
     * Animates the progress level to the target value.
     *
     * @param targetLevel    Target progress value (0-100)
     * @param durationMillis Animation duration in milliseconds
     */
    public void animateToLevel(int targetLevel, long durationMillis) {
        final int startLevel = this.currentProgressLevel;
        final int endLevel = clampValue(targetLevel, MIN_PROGRESS_LEVEL, MAX_PROGRESS_LEVEL);
        final long animationStartTime = System.currentTimeMillis();

        post(new Runnable() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - animationStartTime;
                float animationProgress = Math.min(1f, (float) elapsedTime / durationMillis);

                // Calculate current level using linear interpolation
                int interpolatedLevel = (int) (startLevel + (endLevel - startLevel) * animationProgress);
                setProgressLevel(interpolatedLevel);

                // Continue animation if not complete
                if (animationProgress < 1f) {
                    postDelayed(this, ANIMATION_FRAME_INTERVAL_MS);
                }
            }
        });
    }

    /**
     * Alias for animateToLevel (backward compatibility).
     */
    public void animateTo(int targetLevel, long durationMillis) {
        animateToLevel(targetLevel, durationMillis);
    }

    /**
     * Sets the track background color.
     *
     * @param color New track color (ColorInt)
     */
    public void setTrackColor(int color) {
        trackBackgroundPaint.setColor(color);
        invalidate();
    }

    /**
     * Sets the progress indicator color.
     *
     * @param color New progress color (ColorInt)
     */
    public void setProgressColor(int color) {
        progressIndicatorPaint.setColor(color);
        invalidate();
    }

    // ==================== HELPER METHODS ====================

    /**
     * Clamps a value between minimum and maximum bounds.
     */
    private int clampValue(int value, int minValue, int maxValue) {
        return Math.max(minValue, Math.min(maxValue, value));
    }

    /**
     * Converts DP value to pixels.
     */
    private float convertDpToPixels(float dpValue) {
        return dpValue * getResources().getDisplayMetrics().density;
    }
}
