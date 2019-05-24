/*
 * Created by Dominik Szaradowski on 24.05.19 09:35
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.Date;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.adapters.ProductsAdapter;
import pl.szaradowski.mycart.adapters.ReceiptsAdapter;
import pl.szaradowski.mycart.common.Receipt;
import pl.szaradowski.mycart.components.IconButton;
import pl.szaradowski.mycart.components.RichTextView;

public class ReceiptActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    RecyclerView list;
    ProductsAdapter adapter;

    RichTextView title;
    IconButton menu, back, add;
    Receipt receipt;
    int receipt_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        title = findViewById(R.id.title);
        menu = findViewById(R.id.menu);
        back = findViewById(R.id.back);
        add = findViewById(R.id.add);

        list = findViewById(R.id.list);

        Intent intent = getIntent();
        receipt_id = intent.getIntExtra("receipt_id", -1);

        if(receipt_id == -1){
            Date currentTime = Calendar.getInstance().getTime();

            receipt = Receipt.add("");
            receipt.setName(getString(R.string.receipt_def_name)+" "+receipt.getId());
            receipt.setDate(currentTime);

            receipt_id = receipt.getId();
        }else{
            receipt = Receipt.getById(receipt_id);
        }

        if(receipt != null) title.setText(receipt.getName());

        back.setOnClickListener(new IconButton.OnClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
        menu.setOnClickListener(new IconButton.OnClickListener() {
            @Override
            public void onClick() {
                showMenu();
            }
        });
        add.setOnClickListener(new IconButton.OnClickListener() {
            @Override
            public void onClick() {

            }
        });

        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductsAdapter(receipt.getProducts(), this);
        list.setAdapter(adapter);
    }

    public void showMenu(){
        PopupMenu popup = new PopupMenu(ReceiptActivity.this, menu);
        popup.setOnMenuItemClickListener(ReceiptActivity.this);

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_products, popup.getMenu());

        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        String action = (String) menuItem.getTitleCondensed();

        if(action.equals("changename")){

        }

        if(action.equals("delete")){
            Receipt.removeById(receipt_id);
            finish();
        }

        return false;
    }
}
