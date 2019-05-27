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

    public static void saveAll(Context ctx){
        JsonObject receipt = Receipt.allToJson();

        Gson g = new Gson();
        File f = new File(ctx.getFilesDir(), "data_receipts.json");

        String serialjson = g.toJson(receipt);

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(f));
            outputStreamWriter.write(serialjson);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor preferencesEditor = ctx.getSharedPreferences("settings", Activity.MODE_PRIVATE).edit();
        preferencesEditor.putString("currency", g.toJson(currency.getJson()));
        preferencesEditor.commit();
    }

    public static void loadAll(Context ctx){
        JsonParser parser = new JsonParser();

        Currency.addCurrency(new Currency("EUR", "Euro", "", " €"));
        Currency.addCurrency(new Currency("USD", "Dolar amerykański", "$", ""));
        Currency.addCurrency(new Currency("GBP", "Brytyjski funt szterling", "£", ""));
        Currency.addCurrency(new Currency("PLN", "Polski złoty", "", " zł"));
        Currency.addCurrency(new Currency("RUB", "Rubel rosyjski", "", " руб"));
        Currency.addCurrency(new Currency("CNY", "Chiński yuan", "", " ¥"));

        SharedPreferences preferences = ctx.getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String currency = preferences.getString("currency", "");

        if(currency.length() > 0){
            Utils.currency = new Currency(parser.parse(currency).getAsJsonObject());
        }else{
            Utils.currency = Currency.getById(ctx.getString(R.string.def_currency));
        }

        try {
            Receipt.getList().clear();

            File file_receipt = new File(ctx.getFilesDir(), "data_receipts.json");
            FileInputStream fin = new FileInputStream(file_receipt);
            String ret = convertStreamToString(fin);

            JsonObject json = parser.parse(ret).getAsJsonObject();

            Receipt.last_id = json.get("last_id").getAsInt();
            JsonArray receipts = json.getAsJsonArray("receipts");

            for(JsonElement elr : receipts){
                new Receipt(elr.getAsJsonObject());
            }

            fin.close();

            Receipt.sort();
        } catch (Exception e) {
            Log.e("Utils Saver", "Nothing to load: " + e);
        }
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;

        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }

        reader.close();
        return sb.toString();
    }
}
