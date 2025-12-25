package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class RSGridBackgroundView extends View {

    private int rows = 4;
    private int cols = 4;
    private int lineColor = Color.RED;
    private float lineWidthPx = dpToPx(1f);
    private float contentPaddingPx = 0f;
    private boolean showOuterBorder = true;

    private Paint paint;

    public RSGridBackgroundView(Context context) {
        super(context);
        init(null);
    }

    public RSGridBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RSGridBackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidthPx);
        paint.setColor(lineColor);

        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.GridBackgroundView);
            rows = ta.getInt(R.styleable.GridBackgroundView_gridRows, rows);
            cols = ta.getInt(R.styleable.GridBackgroundView_gridColumns, cols);
            lineColor = ta.getColor(R.styleable.GridBackgroundView_gridLineColor, lineColor);
            lineWidthPx = ta.getDimension(R.styleable.GridBackgroundView_gridLineWidth, lineWidthPx);
            contentPaddingPx = ta.getDimension(R.styleable.GridBackgroundView_gridPadding, contentPaddingPx);
            showOuterBorder = ta.getBoolean(R.styleable.GridBackgroundView_gridShowOuterBorder, showOuterBorder);
            ta.recycle();

            paint.setColor(lineColor);
            paint.setStrokeWidth(lineWidthPx);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float left = getPaddingLeft() + contentPaddingPx;
        float top = getPaddingTop() + contentPaddingPx;
        float right = getWidth() - getPaddingRight() - contentPaddingPx;
        float bottom = getHeight() - getPaddingBottom() - contentPaddingPx;

        if (right <= left || bottom <= top) return;

        float cellW = (right - left) / cols;
        float cellH = (bottom - top) / rows;

        // Dikey çizgiler
        for (int i = 1; i < cols; i++) {
            float x = left + i * cellW;
            canvas.drawLine(x, top, x, bottom, paint);
        }

        // Yatay çizgiler
        for (int j = 1; j < rows; j++) {
            float y = top + j * cellH;
            canvas.drawLine(left, y, right, y, paint);
        }

        // Dış sınır
        if (showOuterBorder) {
            canvas.drawRect(left, top, right, bottom, paint);
        }
    }

    // Public API
    public void setGrid(int rows, int cols) {
        this.rows = Math.max(1, rows);
        this.cols = Math.max(1, cols);
        invalidate();
    }

    public void setLineColor(int color) {
        this.lineColor = color;
        paint.setColor(color);
        invalidate();
    }

    public void setLineWidthDp(float dp) {
        this.lineWidthPx = dpToPx(dp);
        paint.setStrokeWidth(lineWidthPx);
        invalidate();
    }

    public void setPaddingDp(float dp) {
        this.contentPaddingPx = dpToPx(dp);
        invalidate();
    }

    public void setOuterBorderEnabled(boolean enabled) {
        this.showOuterBorder = enabled;
        invalidate();
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
