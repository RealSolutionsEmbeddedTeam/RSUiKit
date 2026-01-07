package com.realsolutions.uikit;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

public class RSToast {

    public static void show(
            @NonNull View parent,
            @NonNull String message,
            @NonNull RSToastType type
    ) {
        Snackbar snackbar = Snackbar.make(parent, "", Snackbar.LENGTH_LONG);

        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(Color.TRANSPARENT);
        layout.setPadding(0, 0, 0, 0);
        layout.setClipToPadding(false);
        layout.setClipChildren(false);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rs_toast, layout, false);

        // içerik
        TextView tv = view.findViewById(R.id.tvMessage);
        ImageView iv = view.findViewById(R.id.ivIcon);
        View root = view.findViewById(R.id.rsToastRoot);

        tv.setText(message);
        iv.setImageResource(RSToastStyle.getIcon(type));

        GradientDrawable bg = (GradientDrawable) root.getBackground().mutate();
        bg.setColor(RSToastStyle.getBackgroundColor(parent.getContext(), type));

        // ✅ ORTALAMA: child view layout params
        Snackbar.SnackbarLayout.LayoutParams lp =
                new Snackbar.SnackbarLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;

        // istersen alt boşluk da:
        lp.bottomMargin = dp(parent.getContext(), 16);

        layout.addView(view, 0, lp);
        snackbar.show();
    }

    private static int dp(Context c, int dp) {
        return (int) (dp * c.getResources().getDisplayMetrics().density + 0.5f);
    }
}
