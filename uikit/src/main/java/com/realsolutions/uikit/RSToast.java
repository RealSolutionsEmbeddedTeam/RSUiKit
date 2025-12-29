package com.realsolutions.uikit;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
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

        Snackbar.SnackbarLayout layout =
                (Snackbar.SnackbarLayout) snackbar.getView();

        layout.setBackgroundColor(Color.TRANSPARENT);
        layout.setPadding(0, 0, 0, 0);

        Snackbar.SnackbarLayout snackbarLayout =
                (Snackbar.SnackbarLayout) snackbar.getView();

        snackbarLayout.setPadding(0, 0, 0, 0);
        snackbarLayout.setClipToPadding(false);
        snackbarLayout.setClipChildren(false);

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rs_toast, layout, false);

        ImageView icon = view.findViewById(R.id.ivIcon);
        TextView text = view.findViewById(R.id.tvMessage);
        LinearLayout root = view.findViewById(R.id.rsToastRoot);

        text.setText(message);
        icon.setImageResource(RSToastStyle.getIcon(type));
        GradientDrawable bg = (GradientDrawable) root.getBackground().mutate();

        bg.setColor(RSToastStyle.getBackgroundColor(parent.getContext(), type));

        layout.addView(view, 0);
        snackbar.show();
    }
}
