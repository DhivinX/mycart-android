/*
 * Created by Dominik Szaradowski on 22.05.19 13:12
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.components.RichTextView;

public class ReceiptsAdapter extends RecyclerView.Adapter<ReceiptsAdapter.ViewHolder> {
    private ArrayList<ReceiptsItem> data;
    private Context ctx;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RichTextView name;
        public RichTextView subname;
        public RichTextView date;
        public ImageView dot;

        private ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            subname = v.findViewById(R.id.subname);
            date = v.findViewById(R.id.date);
            dot = v.findViewById(R.id.dot);
        }
    }

    public ReceiptsAdapter(ArrayList<ReceiptsItem> data, Context ctx) {
        this.data = data;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receipt, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        ReceiptsItem item = data.get(i);

        holder.name.setText(item.name);

        if(i % 3 == 0) holder.dot.setBackground(ContextCompat.getDrawable(ctx, R.drawable.circle_shape_pink));
        else if(i % 2 == 0) holder.dot.setBackground(ContextCompat.getDrawable(ctx, R.drawable.circle_shape_cyan));
        else holder.dot.setBackground(ContextCompat.getDrawable(ctx, R.drawable.circle_shape_orange));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
