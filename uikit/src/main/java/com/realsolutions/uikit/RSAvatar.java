package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.RelativeCornerSize;
import com.google.android.material.shape.ShapeAppearanceModel;

/**
 * RSAvatar Component
 * Supports multiple sizes, variants (image, text, icon, memoji), and color
 * schemes.
 */
public class RSAvatar extends FrameLayout {

    public static final int SIZE_88 = 0;
    public static final int SIZE_72 = 1;
    public static final int SIZE_64 = 2;
    public static final int SIZE_56 = 3;
    public static final int SIZE_48 = 4;
    public static final int SIZE_36 = 5;
    public static final int SIZE_32 = 6;
    public static final int SIZE_28 = 7;
    public static final int SIZE_24 = 8;
    public static final int SIZE_20 = 9;
    public static final int SIZE_16 = 10;

    public static final int VARIANT_IMAGE = 0;
    public static final int VARIANT_TEXT = 1;
    public static final int VARIANT_ICON = 2;
    public static final int VARIANT_MEMOJI = 3;

    public static final int TYPE_NEUTRAL = 0;
    public static final int TYPE_BRAND = 1;
    public static final int TYPE_SUCCESS = 2;
    public static final int TYPE_WARNING = 3;
    public static final int TYPE_ERROR = 4;
    public static final int TYPE_YELLOW = 5;
    public static final int TYPE_ORANGE = 6;
    public static final int TYPE_PURPLE = 7;

    private View bgView;
    private ShapeableImageView imageView;
    private TextView textView;
    private ImageView iconView;
    private View borderView;

    private int size = SIZE_48;
    private int variant = VARIANT_ICON;
    private int type = TYPE_NEUTRAL;
    private String text = "";
    private Drawable imageDrawable;
    private Drawable iconDrawable;
    private boolean hasBorder = false;

    public RSAvatar(@NonNull Context context) {
        super(context);
        init(null);
    }

    public RSAvatar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RSAvatar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.rs_avatar, this, true);

        bgView = findViewById(R.id.rs_avatar_bg);
        imageView = findViewById(R.id.rs_avatar_image);
        textView = findViewById(R.id.rs_avatar_text);
        iconView = findViewById(R.id.rs_avatar_icon);
        borderView = findViewById(R.id.rs_avatar_border);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RSAvatar);
            size = a.getInt(R.styleable.RSAvatar_rsAvatarSize, SIZE_48);
            variant = a.getInt(R.styleable.RSAvatar_rsAvatarVariant, VARIANT_ICON);
            type = a.getInt(R.styleable.RSAvatar_rsAvatarType, TYPE_NEUTRAL);
            text = a.getString(R.styleable.RSAvatar_rsAvatarText);
            imageDrawable = a.getDrawable(R.styleable.RSAvatar_rsAvatarImage);
            iconDrawable = a.getDrawable(R.styleable.RSAvatar_rsAvatarIcon);
            hasBorder = a.getBoolean(R.styleable.RSAvatar_rsAvatarHasBorder, false);
            a.recycle();
        }

        if (bgView == null || imageView == null || textView == null || iconView == null || borderView == null) {
            return;
        }

        applySize();
        applyVariant();
        applyType();
        applyBorder();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizePx = getSizePx();
        // Force the view to be exactly the size specified in our design system
        int exactlySpec = MeasureSpec.makeMeasureSpec(sizePx, MeasureSpec.EXACTLY);
        super.onMeasure(exactlySpec, exactlySpec);
    }

    private int getSizePx() {
        int resId;
        switch (size) {
            case SIZE_88:
                resId = R.dimen.rs_avatar_size_88;
                break;
            case SIZE_72:
                resId = R.dimen.rs_avatar_size_72;
                break;
            case SIZE_64:
                resId = R.dimen.rs_avatar_size_64;
                break;
            case SIZE_56:
                resId = R.dimen.rs_avatar_size_56;
                break;
            case SIZE_48:
                resId = R.dimen.rs_avatar_size_48;
                break;
            case SIZE_36:
                resId = R.dimen.rs_avatar_size_36;
                break;
            case SIZE_32:
                resId = R.dimen.rs_avatar_size_32;
                break;
            case SIZE_28:
                resId = R.dimen.rs_avatar_size_28;
                break;
            case SIZE_24:
                resId = R.dimen.rs_avatar_size_24;
                break;
            case SIZE_20:
                resId = R.dimen.rs_avatar_size_20;
                break;
            case SIZE_16:
                resId = R.dimen.rs_avatar_size_16;
                break;
            default:
                resId = R.dimen.rs_avatar_size_48;
        }
        return getResources().getDimensionPixelSize(resId);
    }

    private void applySize() {
        int sizePx = getSizePx();

        // Adjust internal components based on size
        float textSize;
        if (sizePx >= 88)
            textSize = 32f;
        else if (sizePx >= 64)
            textSize = 24f;
        else if (sizePx >= 48)
            textSize = 18f;
        else if (sizePx >= 32)
            textSize = 14f;
        else if (sizePx >= 24)
            textSize = 10f;
        else
            textSize = 8f;

        if (textView != null) {
            textView.setTextSize(textSize);
        }

        requestLayout();
    }

    private void applyVariant() {
        if (imageView == null || textView == null || iconView == null)
            return;

        imageView.setVisibility(GONE);
        textView.setVisibility(GONE);
        iconView.setVisibility(GONE);

        switch (variant) {
            case VARIANT_IMAGE:
            case VARIANT_MEMOJI:
                imageView.setVisibility(VISIBLE);
                if (imageDrawable != null) {
                    imageView.setImageDrawable(imageDrawable);
                }
                break;
            case VARIANT_TEXT:
                textView.setVisibility(VISIBLE);
                textView.setText(text != null ? text : "");
                break;
            case VARIANT_ICON:
                iconView.setVisibility(VISIBLE);
                if (iconDrawable != null) {
                    iconView.setImageDrawable(iconDrawable);
                } else {
                    iconView.setImageResource(R.drawable.rs_ic_avatar_placeholder); // Default icon
                }
                break;
        }
    }

    private void applyType() {
        if (bgView == null || textView == null || iconView == null)
            return;

        int bgColor;
        int textColor;

        switch (type) {
            case TYPE_BRAND:
                bgColor = ContextCompat.getColor(getContext(), R.color.rs_bg_brand_primary);
                textColor = ContextCompat.getColor(getContext(), R.color.rs_text_brand_solid);
                break;
            case TYPE_SUCCESS:
                bgColor = ContextCompat.getColor(getContext(), R.color.rs_bg_success_primary);
                textColor = ContextCompat.getColor(getContext(), R.color.rs_text_success_solid);
                break;
            case TYPE_WARNING:
                bgColor = ContextCompat.getColor(getContext(), R.color.rs_bg_warning_primary);
                textColor = ContextCompat.getColor(getContext(), R.color.rs_text_warning_solid);
                break;
            case TYPE_ERROR:
                bgColor = ContextCompat.getColor(getContext(), R.color.rs_bg_error_primary);
                textColor = ContextCompat.getColor(getContext(), R.color.rs_text_error_solid);
                break;
            case TYPE_YELLOW:
                bgColor = ContextCompat.getColor(getContext(), R.color.rs_yellow_200);
                textColor = ContextCompat.getColor(getContext(), R.color.rs_yellow_600);
                break;
            case TYPE_ORANGE:
                bgColor = ContextCompat.getColor(getContext(), R.color.rs_orange_200);
                textColor = ContextCompat.getColor(getContext(), R.color.rs_orange_600);
                break;
            case TYPE_PURPLE:
                bgColor = ContextCompat.getColor(getContext(), R.color.rs_purple_200);
                textColor = ContextCompat.getColor(getContext(), R.color.rs_purple_600);
                break;
            case TYPE_NEUTRAL:
            default:
                bgColor = ContextCompat.getColor(getContext(), R.color.rs_gray_light_200);
                textColor = ContextCompat.getColor(getContext(), R.color.rs_gray_light_600);
                break;
        }

        // Use RelativeCornerSize(0.5f) for a perfect circle regardless of size
        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable(
                ShapeAppearanceModel.builder().setAllCornerSizes(new RelativeCornerSize(0.5f)).build());
        shapeDrawable.setFillColor(ColorStateList.valueOf(bgColor));
        bgView.setBackground(shapeDrawable);

        textView.setTextColor(textColor);
        iconView.setColorFilter(textColor);
    }

    private void applyBorder() {
        if (borderView == null)
            return;

        if (hasBorder) {
            borderView.setVisibility(VISIBLE);
            MaterialShapeDrawable borderDrawable = new MaterialShapeDrawable(
                    ShapeAppearanceModel.builder().setAllCornerSizes(new RelativeCornerSize(0.5f)).build());
            borderDrawable.setFillColor(ColorStateList.valueOf(Color.TRANSPARENT));
            borderDrawable.setStroke(getResources().getDimension(R.dimen.rs_avatar_border_width),
                    ContextCompat.getColor(getContext(), R.color.rs_white));
            borderView.setBackground(borderDrawable);
        } else {
            borderView.setVisibility(GONE);
        }
    }

    // Public API
    public void setSize(int size) {
        this.size = size;
        applySize();
    }

    public void setVariant(int variant) {
        this.variant = variant;
        applyVariant();
    }

    public void setType(int type) {
        this.type = type;
        applyType();
    }

    public void setText(String text) {
        this.text = text;
        if (variant == VARIANT_TEXT && textView != null) {
            textView.setText(text);
        }
    }

    public void setImageDrawable(Drawable drawable) {
        this.imageDrawable = drawable;
        if ((variant == VARIANT_IMAGE || variant == VARIANT_MEMOJI) && imageView != null) {
            imageView.setImageDrawable(drawable);
        }
    }

    public void setIconDrawable(Drawable drawable) {
        this.iconDrawable = drawable;
        if (variant == VARIANT_ICON && iconView != null) {
            iconView.setImageDrawable(drawable);
        }
    }

    public void setHasBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;
        applyBorder();
    }
}
