/*
 * Created by Dominik Szaradowski on 22.05.19 13:12
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pl.szaradowski.mycart.R;

public class ReceiptsAdapter extends RecyclerView.Adapter<ReceiptsAdapter.ViewHolder> {
    ArrayList<ReceiptsItem> data;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        private ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
        }
    }

    public ReceiptsAdapter(ArrayList<ReceiptsItem> data) {
        this.data = data;
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
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
