package com.realsolutions.uikit.select;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class RSSelectStringAdapter extends ArrayAdapter<String> {

    public RSSelectStringAdapter(@NonNull Context context, @NonNull List<String> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView tv = v.findViewById(android.R.id.text1);
        tv.setText(getItem(position));
        tv.setTextSize(14f);
        return v;
    }
}
