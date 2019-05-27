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
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.common.Receipt;
import pl.szaradowski.mycart.common.Utils;
import pl.szaradowski.mycart.components.RichTextView;

public class ReceiptsAdapter extends RecyclerView.Adapter<ReceiptsAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout frame;
        public RichTextView name;
        public RichTextView subname;
        public RichTextView date;
        public ImageView dot;

        private ViewHolder(View v) {
            super(v);

            frame = v.findViewById(R.id.frame);
            name = v.findViewById(R.id.name);
            subname = v.findViewById(R.id.subname);
            date = v.findViewById(R.id.date);
            dot = v.findViewById(R.id.dot);
        }
    }

    public interface FingerListener{
        void onClick(int position);
        boolean onLongClick(int position);
    }

    private ArrayList<Receipt> data;
    private Context ctx;
    private FingerListener listener;

    public ReceiptsAdapter(ArrayList<Receipt> data, Context ctx) {
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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int pos) {
        Receipt item = data.get(pos);

        int products_cnt = item.getProducts().size();
        float price_all = item.getVal();

        holder.name.setText(item.getName());
        holder.subname.setText(ctx.getString(R.string.receipt_subname, products_cnt, Utils.currency.getPrefix()+String.format(Utils.locale, "%.2f", price_all)+Utils.currency.getSuffix()));
        holder.date.setText(item.getTimeString(ctx));

        if(pos % 3 == 0) holder.dot.setBackground(ContextCompat.getDrawable(ctx, R.drawable.circle_shape_pink));
        else if(pos % 2 == 0) holder.dot.setBackground(ContextCompat.getDrawable(ctx, R.drawable.circle_shape_cyan));
        else holder.dot.setBackground(ContextCompat.getDrawable(ctx, R.drawable.circle_shape_orange));

        holder.frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) listener.onClick(pos);
            }
        });

        holder.frame.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(listener != null) return listener.onLongClick(pos);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setFingerListener(FingerListener l){
        this.listener = l;
    }
}
