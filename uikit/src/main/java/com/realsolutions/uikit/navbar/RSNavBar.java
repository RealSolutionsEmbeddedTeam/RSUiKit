package com.realsolutions.uikit.navbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.realsolutions.uikit.R;

import java.util.LinkedHashMap;
import java.util.Map;

public class RSNavBar extends LinearLayout {

    public enum LabelMode { ALWAYS, SELECTED_ONLY, NEVER }
    public enum IndicatorMode { TOP_LINE, DOT, NONE }

    public interface OnItemSelectedListener {
        void onItemSelected(int itemId);
    }

    private final Map<Integer, RSNavBarItemView> itemViews = new LinkedHashMap<>();

    private LabelMode labelMode = LabelMode.SELECTED_ONLY;
    private IndicatorMode indicatorMode = IndicatorMode.TOP_LINE;

    private int selectedItemId = View.NO_ID;

    // defaults (theme token bağlanır)
    private int selectedColor = 0xFF3D5AFE;
    private int unselectedColor = 0xFF8A8F98;
    private int indicatorColor = 0xFF3D5AFE;

    private int iconSizePx; // navbar default icon size

    private float selectedWeight = 1.6f;
    private float normalWeight = 1.0f;
    private long weightAnimDuration = 180L;

    @Nullable private OnItemSelectedListener onItemSelectedListener;

    public RSNavBar(@NonNull Context context) {
        this(context, null);
    }

    public RSNavBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);

        int padH = dp(12);
        int padV = dp(8);
        iconSizePx = dp(24);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RSNavBar);

            int lm = a.getInt(R.styleable.RSNavBar_rsLabelMode, 1);
            labelMode = (lm == 0) ? LabelMode.ALWAYS : (lm == 2) ? LabelMode.NEVER : LabelMode.SELECTED_ONLY;

            int im = a.getInt(R.styleable.RSNavBar_rsIndicatorMode, 0);
            indicatorMode = (im == 2) ? IndicatorMode.NONE : (im == 1) ? IndicatorMode.DOT : IndicatorMode.TOP_LINE;

            iconSizePx = a.getDimensionPixelSize(R.styleable.RSNavBar_rsIconSize, iconSizePx);

            padH = a.getDimensionPixelSize(R.styleable.RSNavBar_rsBarPaddingHorizontal, padH);
            padV = a.getDimensionPixelSize(R.styleable.RSNavBar_rsBarPaddingVertical, padV);

            selectedColor = a.getColor(R.styleable.RSNavBar_rsSelectedColor, selectedColor);
            unselectedColor = a.getColor(R.styleable.RSNavBar_rsUnselectedColor, unselectedColor);
            indicatorColor = a.getColor(R.styleable.RSNavBar_rsIndicatorColor, indicatorColor);

            a.recycle();
        }

        setPadding(padH, padV, padH, padV);
        ViewCompat.setElevation(this, dp(8));
        setMinimumHeight(dp(56)); // stabil görünüm
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        itemViews.clear();

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!(child instanceof RSNavBarItemView)) continue;

            RSNavBarItemView item = (RSNavBarItemView) child;
            int id = item.getItemId();

            if (id == View.NO_ID) {
                id = View.generateViewId();
            }

            // Equal width by weight (default normal)
            LayoutParams lp = (LayoutParams) item.getLayoutParams();
            if (lp == null) lp = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
            lp.width = 0;
            lp.weight = normalWeight;
            item.setLayoutParams(lp);

            // icon size: item override varsa onu kullan
            int itemIconSize = item.getItemIconSizePx() > 0 ? item.getItemIconSizePx() : iconSizePx;
            item.applyIconSizePx(itemIconSize);

            final int finalId = id;
            item.setOnClickListener(v -> {
                if (!v.isEnabled()) return;
                selectItem(finalId, true);
            });

            itemViews.put(finalId, item);
        }

        // default select: first enabled item
        for (Map.Entry<Integer, RSNavBarItemView> e : itemViews.entrySet()) {
            if (e.getValue().isEnabled()) {
                selectItem(e.getKey(), false);
                break;
            }
        }
    }

    public void setOnItemSelectedListener(@Nullable OnItemSelectedListener l) {
        this.onItemSelectedListener = l;
    }

    public void setItemWeights(float normal, float selected) {
        this.normalWeight = normal;
        this.selectedWeight = selected;
        if (selectedItemId != View.NO_ID) selectItem(selectedItemId, false);
    }

    public void setWeightAnimDuration(long durationMs) {
        this.weightAnimDuration = durationMs;
    }

    public void selectItem(int itemId, boolean notify) {
        if (!itemViews.containsKey(itemId)) return;

        selectedItemId = itemId;

        for (Map.Entry<Integer, RSNavBarItemView> e : itemViews.entrySet()) {
            int id = e.getKey();
            RSNavBarItemView item = e.getValue();
            boolean selected = (id == itemId);

            // label visibility logic
            boolean labelVisible;
            switch (labelMode) {
                case ALWAYS: labelVisible = true; break;
                case NEVER: labelVisible = false; break;
                case SELECTED_ONLY:
                default: labelVisible = selected; break;
            }

            // style + indicator
            item.applySelectedStyle(selected, selectedColor, unselectedColor, indicatorColor);
            item.applyIndicatorMode(indicatorMode, selected);

            // premium UX (label + horizontal padding)
            item.animateSelectionUX(selected, labelVisible);

            // weight animation
            float targetWeight = selected ? selectedWeight : normalWeight;
            LayoutParams lp = (LayoutParams) item.getLayoutParams();
            if (lp == null) continue;

            float startWeight = lp.weight;

            if (Math.abs(startWeight - targetWeight) < 0.001f) {
                lp.weight = targetWeight;
                item.requestLayout();
            } else {
                animateWeight(item, startWeight, targetWeight);
            }
        }

        requestLayout();

        if (notify && onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected(itemId);
        }
    }

    public int getSelectedItemId() {
        return selectedItemId;
    }

    public void setBadge(int itemId, @Nullable String text) {
        RSNavBarItemView item = itemViews.get(itemId);
        if (item != null) item.setBadgeText(text);
    }

    // --- Show / Hide ---
    public void show(boolean animated) { toggle(true, animated); }
    public void hide(boolean animated) { toggle(false, animated); }

    public void toggle(boolean visible, boolean animated) {
        if (visible) {
            setVisibility(VISIBLE);
            if (!animated) {
                setAlpha(1f);
                setTranslationY(0f);
                return;
            }
            post(() -> {
                setAlpha(0f);
                setTranslationY(getHeight());
                animate().alpha(1f).translationY(0f).setDuration(200).start();
            });
        } else {
            if (!animated) {
                setVisibility(GONE);
                return;
            }
            post(() -> animate().alpha(0f).translationY(getHeight()).setDuration(200)
                    .withEndAction(() -> setVisibility(GONE))
                    .start());
        }
    }

    private void animateWeight(View child, float from, float to) {
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofFloat(from, to);
        animator.setDuration(weightAnimDuration);
        animator.addUpdateListener(a -> {
            float w = (float) a.getAnimatedValue();
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp != null) {
                lp.weight = w;
                // setLayoutParams yerine requestLayout: daha stabil
                child.requestLayout();
            }
        });
        animator.start();
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
