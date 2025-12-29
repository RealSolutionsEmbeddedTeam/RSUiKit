package com.realsolutions.uikit;

import android.content.Context;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

public class RSToastStyle {

    @DrawableRes
    public static int getIcon(RSToastType type) {
        switch (type) {
            case SUCCESS:
                return R.drawable.rs_ic_success;
            case WARNING:
                return R.drawable.rs_ic_warning;
            case INFO:
                return R.drawable.rs_ic_info;
            case ERROR:
            default:
                return R.drawable.rs_ic_error;
        }
    }

    @ColorInt
    public static int getBackgroundColor(Context context, RSToastType type) {
        switch (type) {
            case SUCCESS:
                return ContextCompat.getColor(context, R.color.rs_toast_success_bg);
            case WARNING:
                return ContextCompat.getColor(context, R.color.rs_toast_warning_bg);
            case INFO:
                return ContextCompat.getColor(context, R.color.rs_toast_info_bg);
            case ERROR:
            default:
                return ContextCompat.getColor(context, R.color.rs_toast_error_bg);
        }
    }
}
