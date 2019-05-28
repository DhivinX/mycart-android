/*
 * Created by Dominik Szaradowski on 24.05.19 09:35
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import java.util.ArrayList;
import java.util.Calendar;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.adapters.ProductsAdapter;
import pl.szaradowski.mycart.common.DBManager;
import pl.szaradowski.mycart.common.Product;
import pl.szaradowski.mycart.common.Receipt;
import pl.szaradowski.mycart.common.Utils;
import pl.szaradowski.mycart.components.IconButton;
import pl.szaradowski.mycart.components.RichEditText;
import pl.szaradowski.mycart.components.RichTextView;

public class ReceiptActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    RecyclerView list;
    ProductsAdapter adapter;
    ArrayList<Product> products = new ArrayList<>();

    RichTextView title, price, receipt_name, subname;
    IconButton menu, back, add;
    Receipt receipt;
    long id_receipt = -1;

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
        id_receipt = intent.getLongExtra("id_receipt", -1);

        if(id_receipt == -1){
            receipt = new Receipt();

            id_receipt = Utils.db.setReceipt(receipt, DBManager.ACTION_INSERT);

            receipt.setId(id_receipt);
            receipt.setName(getString(R.string.receipt_def_name)+" "+receipt.getId());
            receipt.setTime(System.currentTimeMillis());

            Utils.db.setReceipt(receipt, DBManager.ACTION_UPDATE);
        }

        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductsAdapter(products, this);
        list.setAdapter(adapter);

        load();

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
                intent.putExtra("id_receipt", receipt.getId());
                startActivity(intent);
            }
        });

        adapter.setFingerListener(new ProductsAdapter.FingerListener() {
            @Override
            public void onClick(int position) {
                Product item = products.get(position);

                Intent intent = new Intent(ReceiptActivity.this, ProductActivity.class);
                intent.putExtra("id_receipt", receipt.getId());
                intent.putExtra("id_product", item.getId());
                startActivity(intent);
            }

            @Override
            public boolean onLongClick(int position) {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }

    public void load() {
        receipt = Utils.db.getReceiptById(id_receipt);

        products.clear();
        products.addAll(Utils.db.getProductsList(receipt.getId()));
        adapter.notifyDataSetChanged();

        receipt_name.setText(receipt.getName());

        subname.setText(getString(R.string.receipt_subname, receipt.getCnt(), Utils.currency.formatPrice(receipt.getVal())));
        price.setText(Utils.currency.formatPrice(receipt.getVal()));
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
                    load();
                    Utils.db.setReceipt(receipt, DBManager.ACTION_UPDATE);

                    dialog.dismiss();
                }
            });

            builder.show();
        }

        if(action.equals("delete")){
            Utils.db.setReceipt(receipt, DBManager.ACTION_DELETE);
            finish();
        }

        if(action.equals("date")){
            final Calendar date = Calendar.getInstance();
            date.setTimeInMillis(receipt.getTime());

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    date.set(year, month, dayOfMonth);
                    receipt.setTime(date.getTimeInMillis());

                    Utils.db.setReceipt(receipt, DBManager.ACTION_UPDATE);
                }
            }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        }

        return false;
    }
}
