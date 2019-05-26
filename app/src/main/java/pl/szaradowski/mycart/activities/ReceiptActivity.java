/*
 * Created by Dominik Szaradowski on 24.05.19 09:35
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.adapters.ProductsAdapter;
import pl.szaradowski.mycart.common.Product;
import pl.szaradowski.mycart.common.Receipt;
import pl.szaradowski.mycart.common.Utils;
import pl.szaradowski.mycart.components.IconButton;
import pl.szaradowski.mycart.components.RichEditText;
import pl.szaradowski.mycart.components.RichTextView;

public class ReceiptActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    RecyclerView list;
    ProductsAdapter adapter;

    RichTextView title, price, receipt_name, subname;
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
        price = findViewById(R.id.price);
        receipt_name = findViewById(R.id.receipt_name);
        subname = findViewById(R.id.subname);

        list = findViewById(R.id.list);

        Intent intent = getIntent();
        receipt_id = intent.getIntExtra("receipt_id", -1);

        if(receipt_id == -1){
            receipt = Receipt.add("");
            receipt.setName(getString(R.string.receipt_def_name)+" "+receipt.getId());
            receipt.setTime(System.currentTimeMillis());

            receipt_id = receipt.getId();

            Receipt.sort();
        }else{
            receipt = Receipt.getById(receipt_id);
        }

        title.setText(R.string.receipt_list);

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
                Intent intent = new Intent(ReceiptActivity.this, ProductActivity.class);
                intent.putExtra("receipt_id", receipt.getId());
                startActivity(intent);
            }
        });

        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductsAdapter(receipt.getProducts(), this);
        list.setAdapter(adapter);

        adapter.setFingerListener(new ProductsAdapter.FingerListener() {
            @Override
            public void onClick(int position) {
                Product item = receipt.getProducts().get(position);

                Intent intent = new Intent(ReceiptActivity.this, ProductActivity.class);
                intent.putExtra("receipt_id", receipt.getId());
                intent.putExtra("product_id", item.getId());
                startActivity(intent);
            }

            @Override
            public boolean onLongClick(int position) {
                return false;
            }
        });

        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh() {
        if (receipt != null) {
            adapter.notifyDataSetChanged();

            int products_cnt = receipt.getProducts().size();
            float price_all = receipt.getVal();

            receipt_name.setText(receipt.getName());

            subname.setText(getString(R.string.receipt_subname, products_cnt, String.format(Utils.locale, "%.2f", price_all), receipt.getCurrency()));
            price.setText(String.format(Utils.locale, "%.2f", price_all) + " " + Utils.currency);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Utils.saveReceipts(ReceiptActivity.this);
                }
            });
        }
    }

    public void showMenu(){
        PopupMenu popup = new PopupMenu(ReceiptActivity.this, menu);
        popup.setOnMenuItemClickListener(ReceiptActivity.this);

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_receipt, popup.getMenu());

        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        String action = (String) menuItem.getTitleCondensed();

        if(action.equals("changename")){
            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiptActivity.this);
            builder.setIcon(R.drawable.icon_text_dark);
            builder.setTitle(R.string.receipt_changename_dialog_title);

            View v = getLayoutInflater().inflate(R.layout.dialog_receipt_changename, null);

            final RichEditText etName = v.findViewById(R.id.etName);

            builder.setView(v);

            etName.setText(receipt.getName());

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    receipt.setName(etName.getText().toString());

                    refresh();

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Utils.saveReceipts(ReceiptActivity.this);
                        }
                    });

                    dialog.dismiss();
                }
            });

            builder.show();
        }

        if(action.equals("delete")){
            Receipt.removeById(ReceiptActivity.this, receipt_id);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Utils.saveReceipts(ReceiptActivity.this);
                }
            });

            finish();
        }

        return false;
    }
}
