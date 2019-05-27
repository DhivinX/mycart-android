/*
 * Created by Dominik Szaradowski on 23.05.19 10:44
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import com.google.android.gms.vision.text.TextBlock;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.activities.ProductActivity;
import pl.szaradowski.mycart.common.Currency;
import pl.szaradowski.mycart.common.Utils;
import pl.szaradowski.mycart.components.RichButton;

public class SettingsFragment extends Fragment {
    RichButton btnCurrency;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnCurrency = view.findViewById(R.id.btnCurrency);

        btnCurrency.setText(Utils.currency.getId() + " - " + Utils.currency.getName());

        btnCurrency.setOnClickListener(new RichButton.OnClickListener() {
            @Override
            public void onClick() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setIcon(R.drawable.icon_change_dark);
                builder.setTitle(R.string.settings_currency_title);

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item);

                for(Currency c : Currency.getAll()){
                    arrayAdapter.add(c.getId() + " - " + c.getName());
                }

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.currency = Currency.getAll().get(which);
                        btnCurrency.setText(Utils.currency.getId() + " - " + Utils.currency.getName());

                        Utils.saveAll(getContext());
                    }
                });

                builder.show();
            }
        });
    }
}
