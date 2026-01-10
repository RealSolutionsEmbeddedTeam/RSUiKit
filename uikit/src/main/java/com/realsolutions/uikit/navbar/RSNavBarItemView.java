package com.realsolutions.uikit.navbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.realsolutions.uikit.R;

public class RSNavBarItemView extends FrameLayout {

    private View indicator;
    private View content;
    private View iconBox;
    private ImageView icon;
    private TextView label;
    private TextView badge;

    private int itemId = View.NO_ID;
    @DrawableRes private int iconRes = 0;
    private String title = null;
    private boolean itemEnabled = true;
    private String badgeText = null;

    // item-level override; 0 => use RSNavBar default icon size
    private int itemIconSizePx = 0;

    public RSNavBarItemView(@NonNull Context context) {
        this(context, null);
    }

    public RSNavBarItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.rs_nav_bar_item, this, true);
        setClickable(true);
        setFocusable(true);

        indicator = findViewById(R.id.rs_nav_indicator);
        content = findViewById(R.id.rs_nav_content);
        iconBox = findViewById(R.id.rs_nav_icon_box);
        icon = findViewById(R.id.rs_nav_icon);
        label = findViewById(R.id.rs_nav_label);
        badge = findViewById(R.id.rs_nav_badge);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RSNavBarItem);

            itemId = a.getResourceId(R.styleable.RSNavBarItem_rsItemId, View.NO_ID);
            if (itemId == View.NO_ID) itemId = a.getInt(R.styleable.RSNavBarItem_rsItemId, View.NO_ID);

            iconRes = a.getResourceId(R.styleable.RSNavBarItem_rsIcon, 0);
            title = a.getString(R.styleable.RSNavBarItem_rsLabel);
            itemEnabled = a.getBoolean(R.styleable.RSNavBarItem_rsEnabled, true);
            badgeText = a.getString(R.styleable.RSNavBarItem_rsBadge);

            itemIconSizePx = a.getDimensionPixelSize(R.styleable.RSNavBarItem_rsItemIconSize, 0);

            a.recycle();
        }

        applyRawValues();
    }

    private void applyRawValues() {
        setEnabled(itemEnabled);

        if (iconRes != 0) icon.setImageResource(iconRes);
        label.setText(title == null ? "" : title);

        setBadgeText(badgeText);

        // label default invis (yer tutsun)
        if (label.getVisibility() != INVISIBLE && label.getVisibility() != VISIBLE) {
            label.setVisibility(INVISIBLE);
        }
    }

    public int getItemId() { return itemId; }
    public int getItemIconSizePx() { return itemIconSizePx; }

    public void bindProgrammatic(int id, @DrawableRes int iconRes, @Nullable String title, boolean enabled) {
        this.itemId = id;
        this.iconRes = iconRes;
        this.title = title;
        this.itemEnabled = enabled;
        applyRawValues();
    }

    /** Parent'ı ne olursa olsun güvenli: FrameLayout/LinearLayout cast yok */
    public void applyIconSizePx(int px) {
        ViewGroup.LayoutParams lp = iconBox.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(px, px);
        } else {
            lp.width = px;
            lp.height = px;
        }
        iconBox.setLayoutParams(lp);
    }

    public void setBadgeText(@Nullable String text) {
        if (text == null || text.trim().isEmpty()) {
            badge.setVisibility(GONE);
            return;
        }
        badge.setText(text);
        badge.setVisibility(VISIBLE);
    }

    /** Zıplama olmasın diye indicator asla GONE değil, INVISIBLE kullan */
    public void applyIndicatorMode(RSNavBar.IndicatorMode mode, boolean selected) {
        if (mode == RSNavBar.IndicatorMode.NONE) {
            indicator.setVisibility(INVISIBLE);
            return;
        }

        if (mode == RSNavBar.IndicatorMode.TOP_LINE) {
            indicator.setVisibility(selected ? VISIBLE : INVISIBLE);
            return;
        }

        // DOT enum dursun, şimdilik yok:
        indicator.setVisibility(INVISIBLE);
    }

    public void applySelectedStyle(boolean selected, int selectedColor, int unselectedColor, int indicatorColor) {
        setSelected(selected);

        icon.setColorFilter(selected ? selectedColor : unselectedColor);

        label.setTextColor(selected ? selectedColor : unselectedColor);
        label.setTypeface(label.getTypeface(), selected ? Typeface.BOLD : Typeface.NORMAL);

        indicator.setBackgroundColor(indicatorColor);

        // Weight animasyonu varken scale kullanmıyoruz (stabil)
        setScaleX(1f);
        setScaleY(1f);

        setAlpha(isEnabled() ? 1f : 0.45f);

        // badge görünüyorsa style uygula
        if (badge.getVisibility() == VISIBLE) {
            badge.setTextColor(0xFFFFFFFF);
            badge.setBackground(makeBadgeBg(selectedColor));
        }
    }

    /** Label + horizontal padding micro anim (dikey SABİT => zıplama yok) */
    public void animateSelectionUX(boolean selected, boolean labelShouldBeVisible) {
        // Label fade + slide (GONE değil INVISIBLE)
        if (labelShouldBeVisible) {
            label.setVisibility(VISIBLE);
            label.setAlpha(0f);
            label.setTranslationX(dp(6));
            label.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(160)
                    .start();
        } else {
            if (label.getVisibility() == VISIBLE) {
                label.animate()
                        .alpha(0f)
                        .translationX(dp(6))
                        .setDuration(140)
                        .withEndAction(() -> {
                            label.setVisibility(INVISIBLE);
                            label.setAlpha(1f);
                            label.setTranslationX(0f);
                        })
                        .start();
            } else {
                label.setVisibility(INVISIBLE);
            }
        }

        // Only horizontal padding anim
        int baseH = dp(12);
        int selH = dp(14);
        int targetPH = selected ? selH : baseH;

        animateHorizontalPadding(content, targetPH);
    }

    private void animateHorizontalPadding(View v, int targetPH) {
        int startL = v.getPaddingLeft();
        int startR = v.getPaddingRight();

        int endL = targetPH;
        int endR = targetPH;

        final int top = v.getPaddingTop();       // sabit
        final int bottom = v.getPaddingBottom(); // sabit

        android.animation.ValueAnimator anim = android.animation.ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(160);
        anim.addUpdateListener(a -> {
            float t = (float) a.getAnimatedValue();
            int l = (int) (startL + (endL - startL) * t);
            int r = (int) (startR + (endR - startR) * t);
            v.setPadding(l, top, r, bottom);
        });
        anim.start();
    }

    private GradientDrawable makeBadgeBg(int bgColor) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(bgColor);
        d.setCornerRadius(dp(999));
        return d;
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }
}
