/*
 * Created by Dominik Szaradowski on 22.05.19 11:21
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Random;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.adapters.ReceiptsAdapter;
import pl.szaradowski.mycart.adapters.ReceiptsItem;

public class ReceiptsFragment extends Fragment {
    RecyclerView list;
    ArrayList<ReceiptsItem> items = new ArrayList<>();
    ReceiptsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_receipts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = view.findViewById(R.id.list);

        for(int i = 0; i < 100; i++){
            ReceiptsItem it = new ReceiptsItem();

            Random rand = new Random();
            it.name = "Paragon nr "+rand.nextInt(50);

            items.add(it);
        }

        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReceiptsAdapter(items);
        list.setAdapter(adapter);
    }
}
