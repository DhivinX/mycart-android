/*
 * Created by Dominik Szaradowski on 22.05.19 11:20
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.adapters.ReceiptsAdapter;
import pl.szaradowski.mycart.adapters.ReceiptsItem;
import pl.szaradowski.mycart.components.IconButton;

public class MainActivity extends AppCompatActivity {
    IconButton btnSettings, btnReceipts, btnCharts;

    RecyclerView list;
    ArrayList<ReceiptsItem> items = new ArrayList<>();
    ReceiptsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.list);

        for(int i = 0; i < 100; i++){
            ReceiptsItem it = new ReceiptsItem();

            Random rand = new Random();
            it.name = "Paragon nr "+rand.nextInt(50);

            items.add(it);
        }

        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReceiptsAdapter(items);
        list.setAdapter(adapter);
    }
}
