/*
 * Created by Dominik Szaradowski on 24.05.19 15:16
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import pl.szaradowski.mycart.R;

public class Utils {
    public static Currency currency;
    public static int[] types = {0, 1, 2, 3};
    public static DBManager db;

    public static void saveAll(Context ctx){
        Gson g = new Gson();

        SharedPreferences.Editor preferencesEditor = ctx.getSharedPreferences("settings", Activity.MODE_PRIVATE).edit();
        preferencesEditor.putString("currency", g.toJson(currency.getJson()));
        preferencesEditor.commit();
    }

    public static void loadAll(Context ctx){
        JsonParser parser = new JsonParser();

        SharedPreferences preferences = ctx.getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String currency = preferences.getString("currency", "");

        if(currency.length() > 0){
            Utils.currency = new Currency(parser.parse(currency).getAsJsonObject());
        }else{
            Utils.currency = Currency.getById(ctx.getString(R.string.def_currency));
        }
    }
}
