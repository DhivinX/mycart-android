/*
 * Created by Dominik Szaradowski on 24.05.19 15:16
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
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
import java.util.Random;

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

    public static File saveBitmap(String name, Bitmap bm){
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/HowMuch");
        myDir.mkdirs();

        File file = new File(myDir, name);

        if (file.exists()) file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }
}
