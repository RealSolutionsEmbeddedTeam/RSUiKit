package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * RSAvatarGroup Component
 * Manages multiple RSAvatar instances with overlapping effect.
 */
public class RSAvatarGroup extends LinearLayout {

    private int groupSize = RSAvatar.SIZE_48;
    private int maxAvatars = 5;
    private int overlapMargin;

    public RSAvatarGroup(Context context) {
        super(context);
        init(null);
    }

    public RSAvatarGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RSAvatarGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        setOrientation(HORIZONTAL);
        overlapMargin = getResources().getDimensionPixelSize(R.dimen.rs_avatar_group_overlap);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RSAvatarGroup);
            groupSize = a.getInt(R.styleable.RSAvatarGroup_rsAvatarGroupSize, RSAvatar.SIZE_48);
            maxAvatars = a.getInt(R.styleable.RSAvatarGroup_rsAvatarGroupMax, 5);
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        updateChildren();
    }

    public void updateChildren() {
        int childCount = getChildCount();
        if (childCount == 0)
            return;

        List<View> children = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            children.add(getChildAt(i));
        }

        // Limitation logic if needed can be added here
        // For now, let's apply the group size and overlap to all children

        for (int i = 0; i < children.size(); i++) {
            View v = children.get(i);
            if (v instanceof RSAvatar) {
                RSAvatar avatar = (RSAvatar) v;
                avatar.setSize(groupSize);
                avatar.setHasBorder(true);

                ViewGroup.LayoutParams vlp = avatar.getLayoutParams();
                if (vlp instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) vlp;
                    if (i > 0) {
                        lp.setMarginStart(overlapMargin);
                    } else {
                        lp.setMarginStart(0);
                    }
                    avatar.setLayoutParams(lp);
                }
            }
        }
    }

    // Public API
    public void setGroupSize(int size) {
        this.groupSize = size;
        updateChildren();
    }

    public void setMaxAvatars(int max) {
        this.maxAvatars = max;
        // Re-layout would be needed if we implement the +N logic dynamically
    }
}
