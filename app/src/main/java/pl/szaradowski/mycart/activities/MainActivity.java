/*
 * Created by Dominik Szaradowski on 22.05.19 11:20
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.activities;

import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Random;

import pl.szaradowski.mycart.R;
import pl.szaradowski.mycart.adapters.ReceiptsAdapter;
import pl.szaradowski.mycart.adapters.ReceiptsItem;
import pl.szaradowski.mycart.common.Screen;
import pl.szaradowski.mycart.components.IconButton;
import pl.szaradowski.mycart.components.RichTextView;
import pl.szaradowski.mycart.fragments.ChartsFragment;
import pl.szaradowski.mycart.fragments.ReceiptsFragment;
import pl.szaradowski.mycart.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {
    CoordinatorLayout coordinator;
    AppBarLayout appbar;
    IconButton btnSettings, btnReceipts, btnCharts;
    Screen scrReceipts, scrCharts, scrSettings;

    ImageView ivMainBackground, ivLogo;
    RichTextView tvLogoText, tvLogoTextSub;
    FrameLayout flLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrReceipts = new Screen(this, new ReceiptsFragment(), "receipts", null);
        scrCharts = new Screen(this, new ChartsFragment(), "charts", scrReceipts);
        scrSettings = new Screen(this, new SettingsFragment(), "settings", scrReceipts);

        coordinator = findViewById(R.id.coordinator);
        appbar = findViewById(R.id.appbar);
        ivMainBackground = findViewById(R.id.ivMainBackground);
        flLogo = findViewById(R.id.flLogo);
        ivLogo = findViewById(R.id.ivLogo);
        tvLogoText = findViewById(R.id.tvLogoText);
        tvLogoTextSub = findViewById(R.id.tvLogoTextSub);
        btnSettings = findViewById(R.id.btnSettings);
        btnReceipts = findViewById(R.id.btnReceipts);
        btnCharts = findViewById(R.id.btnCharts);

        Animation animateBackground = AnimationUtils.loadAnimation(this, R.anim.scale_background);
        ivMainBackground.startAnimation(animateBackground);

        scrReceipts.Load();

        btnReceipts.setOnClickListener(new IconButton.OnClickListener(){
            @Override
            public void onClick() {
                appbar.setExpanded(true,true);
                scrReceipts.Load();
            }
        });

        btnCharts.setOnClickListener(new IconButton.OnClickListener(){
            @Override
            public void onClick() {
                appbar.setExpanded(true,true);
                scrCharts.Load();
            }
        });

        btnSettings.setOnClickListener(new IconButton.OnClickListener(){
            @Override
            public void onClick() {
                appbar.setExpanded(true,true);
                scrSettings.Load();
            }
        });

        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                float dp = getResources().getDisplayMetrics().density;
                float ratio = 1 - (float) Math.abs(i) / appBarLayout.getTotalScrollRange();

                if(ratio > 1) ratio = 1;
                if(ratio < 0) ratio = 0;

                int alpha = Math.round(ratio * 255);
                int alpha2 = Math.round(ratio * 119);

                ivLogo.setAlpha(ratio);
                tvLogoText.setTextColor(Color.argb(alpha, 255, 255, 255));
                tvLogoTextSub.setTextColor(Color.argb(alpha2, 255, 255, 255));

                float lg = (1 - ratio) * 50*dp;
                CoordinatorLayout.LayoutParams cl = new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                cl.setMargins(Math.round(20*dp), Math.round(85*dp - lg), 0, 0);
                flLogo.setLayoutParams(cl);

                float im = (1 - ratio) * 40*dp;
                cl = new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                cl.setMargins(0, Math.round(0 - im), 0, 0);
                ivMainBackground.setLayoutParams(cl);
            }
        });
    }

    @Override
    public void onBackPressed() {
        appbar.setExpanded(true,true);
        Screen.onBack(this);
    }
}
