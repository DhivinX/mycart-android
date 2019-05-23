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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.adapters.ReceiptsAdapter;
import pl.szaradowski.mycart.adapters.ReceiptsItem;
import pl.szaradowski.mycart.common.Screen;
import pl.szaradowski.mycart.components.IconButton;
import pl.szaradowski.mycart.fragments.ChartsFragment;
import pl.szaradowski.mycart.fragments.ReceiptsFragment;
import pl.szaradowski.mycart.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {
    IconButton btnSettings, btnReceipts, btnCharts;
    Screen scrReceipts, scrCharts, scrSettings;

    ImageView ivMainBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrReceipts = new Screen(this, new ReceiptsFragment(), "receipts", null);
        scrCharts = new Screen(this, new ChartsFragment(), "charts", scrReceipts);
        scrSettings = new Screen(this, new SettingsFragment(), "settings", scrReceipts);

        ivMainBackground = findViewById(R.id.ivMainBackground);
        btnSettings = findViewById(R.id.btnSettings);
        btnReceipts = findViewById(R.id.btnReceipts);
        btnCharts = findViewById(R.id.btnCharts);

        Animation animateBackground = AnimationUtils.loadAnimation(this, R.anim.scale_background);
        ivMainBackground.startAnimation(animateBackground);

        scrReceipts.Load();

        btnReceipts.setOnClickListener(new IconButton.OnClickListener(){
            @Override
            public void onClick() {
                scrReceipts.Load();
            }
        });

        btnCharts.setOnClickListener(new IconButton.OnClickListener(){
            @Override
            public void onClick() {
                scrCharts.Load();
            }
        });

        btnSettings.setOnClickListener(new IconButton.OnClickListener(){
            @Override
            public void onClick() {
                scrSettings.Load();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Screen.onBack(this);
    }
}
