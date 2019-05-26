/*
 * Created by Dominik Szaradowski on 24.05.19 14:17
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.common.Product;
import pl.szaradowski.mycart.common.Receipt;
import pl.szaradowski.mycart.common.Settings;
import pl.szaradowski.mycart.components.RichTextView;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout frame;
        public RichTextView name;
        public RichTextView subname;
        public RichTextView price;
        public ImageView dot;
        public ImageView img;

        private ViewHolder(View v) {
            super(v);

            frame = v.findViewById(R.id.frame);
            name = v.findViewById(R.id.name);
            subname = v.findViewById(R.id.subname);
            price = v.findViewById(R.id.price);
            dot = v.findViewById(R.id.dot);
            img = v.findViewById(R.id.img);
        }
    }

    public interface FingerListener{
        void onClick(int position);
        boolean onLongClick(int position);
    }

    private ArrayList<Product> data;
    private Context ctx;
    private FingerListener listener;

    public ProductsAdapter(ArrayList<Product> data, Context ctx) {
        this.data = data;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int pos) {
        Product item = data.get(pos);

        holder.name.setText(item.getName());
        holder.price.setText(String.format(Settings.locale, "%.2f", item.getVal())+" "+item.getCurrency());
        holder.subname.setText(ctx.getString(R.string.item_product_subname, item.getCnt()+""));

        RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(ctx.getResources(), item.getImg(ctx));
        roundedBitmapDrawable.setCircular(true);
        roundedBitmapDrawable.setAntiAlias(true);

        holder.img.setImageDrawable(roundedBitmapDrawable);

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
