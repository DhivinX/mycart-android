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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Objects;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.common.Settings;

public class StartActivity extends AppCompatActivity {
    String[] PERMISSIONS = {
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    Handler h;
    Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!hasPermissions(StartActivity.this, PERMISSIONS)) requestPermissions(PERMISSIONS, 33);
        }

        h = new Handler();
        h.postDelayed(r, 2000);

        r = new Runnable() {
            @Override
            public void run() {
                if(hasPermissions(StartActivity.this, PERMISSIONS)){
                    Settings.loadAll(StartActivity.this);

                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    h.postDelayed(this, 2000);
                }
            }
        };
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
