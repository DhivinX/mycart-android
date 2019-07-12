/*
 * Created by Dominik Szaradowski on 24.05.19 18:42
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.common.Currency;
import pl.szaradowski.mycart.common.DBManager;
import pl.szaradowski.mycart.common.Utils;
import pl.szaradowski.mycart.components.RichButton;
import pl.szaradowski.mycart.components.RichTextView;

public class StartActivity extends AppCompatActivity {
    String[] PERMISSIONS = {
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
            //android.Manifest.permission.RECORD_AUDIO
    };

    Handler h;
    Runnable r;

    RichTextView tvPermText;
    RichButton btnPerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Utils.db = new DBManager(this);

        Currency.addCurrency(new Currency("EUR", getString(R.string.EUR_name), "", " €"));
        Currency.addCurrency(new Currency("USD", getString(R.string.USD_name), "$", ""));
        Currency.addCurrency(new Currency("GBP", getString(R.string.GBP_name), "£", ""));
        Currency.addCurrency(new Currency("PLN", getString(R.string.PLN_name), "", " zł"));
        Currency.addCurrency(new Currency("RUB", getString(R.string.RUB_name), "", " руб"));
        Currency.addCurrency(new Currency("CNY", getString(R.string.CNY_name), "", " ¥"));

        tvPermText = findViewById(R.id.tvPermText);
        btnPerm = findViewById(R.id.btnPerm);

        btnPerm.setOnClickListener(new RichButton.OnClickListener() {
            @Override
            public void onClick() {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(!hasPermissions(StartActivity.this, PERMISSIONS)) requestPermissions(PERMISSIONS, 33);
                }
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!hasPermissions(StartActivity.this, PERMISSIONS)) requestPermissions(PERMISSIONS, 33);
            else {
                tvPermText.setVisibility(View.GONE);
                btnPerm.setVisibility(View.GONE);
            }
        }else{
            tvPermText.setVisibility(View.GONE);
            btnPerm.setVisibility(View.GONE);
        }

        h = new Handler();
        h.postDelayed(r, 2000);

        r = new Runnable() {
            @Override
            public void run() {
                if(hasPermissions(StartActivity.this, PERMISSIONS) || android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                    Utils.loadAll(StartActivity.this);

                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);

                    h.removeCallbacks(this);
                }else{
                    h.postDelayed(this, 2000);
                }
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(hasPermissions(StartActivity.this, PERMISSIONS)) {
            tvPermText.setVisibility(View.GONE);
            btnPerm.setVisibility(View.GONE);
        }else{
            tvPermText.setVisibility(View.VISIBLE);
            btnPerm.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        h.postDelayed(r, 2000);
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
