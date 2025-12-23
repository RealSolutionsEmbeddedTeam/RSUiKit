package com.realsolutions.uikit;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class RSLoadingCircle extends View {

    // attrs
    private int sizePx;
    private float speedDegPerSec = 260f;
    private float trailDeg = 260f;
    private int glowColor = 0xFF3B66FF;   // rsColor default

    // animation
    private float rotationDeg = 0f;
    private ValueAnimator animator;

    // paints
    private final Paint basePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint trailPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF oval = new RectF();

    // colors
    private final int baseColor = 0xFF14161A;     // koyu disk
    private final int transparent = 0x00000000;

    // shaders
    private SweepGradient sweep;
    private RadialGradient radialMask;
    private final Matrix sweepMatrix = new Matrix();

    public RSLoadingCircle(Context c) { super(c); init(null); }
    public RSLoadingCircle(Context c, @Nullable AttributeSet a) { super(c, a); init(a); }
    public RSLoadingCircle(Context c, @Nullable AttributeSet a, int d) { super(c, a, d); init(a); }

    private void init(@Nullable AttributeSet attrs) {
        sizePx = dp(96);

        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.RSLoadingCircle);
            sizePx = ta.getDimensionPixelSize(R.styleable.RSLoadingCircle_rsSize, sizePx);
            speedDegPerSec = ta.getFloat(R.styleable.RSLoadingCircle_rsSpeed, speedDegPerSec);
            trailDeg = ta.getFloat(R.styleable.RSLoadingCircle_rsTrailDeg, trailDeg);
            glowColor = ta.getColor(R.styleable.RSLoadingCircle_rsColor, glowColor);
            ta.recycle();
        }

        basePaint.setStyle(Paint.Style.FILL);
        basePaint.setColor(baseColor);

        trailPaint.setStyle(Paint.Style.FILL);
        trailPaint.setDither(true);
        trailPaint.setFilterBitmap(true);

        setupAnimator();
    }

    private void setupAnimator() {
        if (animator != null) animator.cancel();

        animator = ValueAnimator.ofFloat(0f, 360f);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);

        long durationMs = (long) (360f / Math.max(1f, speedDegPerSec) * 1000f);
        animator.setDuration(Math.max(300, durationMs));

        animator.addUpdateListener(a -> {
            rotationDeg = (float) a.getAnimatedValue();
            invalidate();
        });
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (animator != null) animator.start();
    }

    @Override protected void onDetachedFromWindow() {
        if (animator != null) animator.cancel();
        super.onDetachedFromWindow();
    }

    @Override protected void onMeasure(int wSpec, int hSpec) {
        // rsSize sadece wrap_content ölçümünde etkili olur.
        int w = resolveSize(sizePx, wSpec);
        int h = resolveSize(sizePx, hSpec);
        int s = Math.min(w, h);
        setMeasuredDimension(s, s);
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        oval.set(0, 0, w, h);

        float cx = w / 2f;
        float cy = h / 2f;
        float r = Math.min(cx, cy);

        float pEnd = clamp(trailDeg / 360f, 0f, 1f);

        // HEAD (en parlak) -> sonra arkaya doğru sönümlensin
        // Not: SweepGradient 0..pEnd aralığını “trail” olarak kullanıyor.
        // Biz HEAD'i trail'in SONUNA koyacağız (pEnd), böylece sadece arkada kalır.
        int head = argb(255, glowColor);
        int mid1 = argb(160, glowColor);
        int mid2 = argb(70, glowColor);
        int tail = argb(0, glowColor);

        sweep = new SweepGradient(cx, cy,
                new int[]{tail, mid2, mid1, head, transparent},
                new float[]{0f, pEnd * 0.50f, pEnd * 0.80f, pEnd, Math.min(1f, pEnd + 0.001f)}
        );

        // Delik olmaması için “kesmek” yerine merkezde biraz daha soluk yapan mask:
        // merkez %0.55 opak, kenar %1.0 opak
        radialMask = new RadialGradient(
                cx, cy, r,
                new int[]{0x88FFFFFF, 0xFFFFFFFF},
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP
        );

        // Sweep + radial mask birleşimi
        // DST_IN: sweep çizimi radial alfa ile maskelenir (delik yok, sadece merkez yumuşar)
        ComposeShader composed = new ComposeShader(sweep, radialMask, PorterDuff.Mode.DST_IN);
        trailPaint.setShader(composed);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;

        // 1) Base disk
        canvas.drawOval(oval, basePaint);

        // 2) Trail disk (HEAD önde, trail arkada)
        if (sweep != null) {
            // HEAD'i “şu anki açıya” koymak için:
            // - sweep'in HEAD'i pEnd'de olduğundan, sweep'i trail kadar geri kaydırıyoruz.
            // yön ters ise burada + / - değiştir.
            float localRotate = +(rotationDeg - trailDeg);

            sweepMatrix.reset();
            sweepMatrix.setRotate(localRotate, cx, cy);

            // ComposeShader içinde sweep var; sweep local matrix'i set edilebilir
            // (ComposeShader setLocalMatrix yok, o yüzden sweep'i döndürmek yeterli)
            sweep.setLocalMatrix(sweepMatrix);

            canvas.drawOval(oval, trailPaint);
        }
    }

    // Helpers
    private int argb(int alpha, int rgb) {
        alpha = Math.max(0, Math.min(255, alpha));
        return (alpha << 24) | (rgb & 0x00FFFFFF);
    }

    private float clamp(float v, float mn, float mx) {
        return Math.max(mn, Math.min(mx, v));
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }
}
