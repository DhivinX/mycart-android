/*
 * Created by Dominik Szaradowski on 23.05.19 10:36
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.activities.ReceiptActivity;
import pl.szaradowski.mycart.adapters.ReceiptsAdapter;
import pl.szaradowski.mycart.common.Receipt;
import pl.szaradowski.mycart.components.IconButton;

public class ReceiptsFragment extends Fragment {
    RecyclerView list;
    ReceiptsAdapter adapter;
    IconButton add;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_receipts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = view.findViewById(R.id.list);
        add = view.findViewById(R.id.add);

        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReceiptsAdapter(Receipt.getList(), getContext());
        list.setAdapter(adapter);

        add.setOnClickListener(new IconButton.OnClickListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent(getActivity(), ReceiptActivity.class);
                intent.putExtra("receipt_index", -1);
                Objects.requireNonNull(getActivity()).startActivity(intent);
            }
        });

        adapter.setFingerListener(new ReceiptsAdapter.FingerListener() {
            @Override
            public void onClick(int position) {
                Receipt item = Receipt.getList().get(position);

                Intent intent = new Intent(getActivity(), ReceiptActivity.class);
                intent.putExtra("receipt_id", item.getId());
                Objects.requireNonNull(getActivity()).startActivity(intent);
            }

            @Override
            public boolean onLongClick(int position) {
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        scrollToPosition(0);
        if(adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        scrollToPosition(0);
        if(adapter != null) adapter.notifyDataSetChanged();
    }

    public void scrollToPosition(final int position) {
        if (list != null) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    list.scrollToPosition(position);

                    if (position == 0) {
                        LinearLayoutManager layoutManager = (LinearLayoutManager) list.getLayoutManager();
                        layoutManager.scrollToPositionWithOffset(0, 0);
                    }
                }
            });
        }
    }
}
