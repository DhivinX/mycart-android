/*
 * Created by Dominik Szaradowski on 24.05.19 15:16
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.common;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Locale;

public class Settings {
    public static String currency = "zł";
    public static Locale locale = Locale.GERMAN;

    public static void saveReceipts(Context ctx){
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
    }

    public static void loadAll(Context ctx){
        try {
            File file_receipt = new File(ctx.getFilesDir(), "data_receipts.json");
            FileInputStream fin = new FileInputStream(file_receipt);
            String ret = convertStreamToString(fin);

            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(ret).getAsJsonObject();

            Receipt.last_id = json.get("last_id").getAsInt();
            JsonArray receipts = json.getAsJsonArray("receipts");

            for(JsonElement elr : receipts){
                JsonObject ritem = elr.getAsJsonObject();

                Receipt or = new Receipt();

                or.setId(ritem.get("id").getAsInt());
                or.setProduct_last_id(ritem.get("product_last_id").getAsInt());
                or.setName(ritem.get("name").getAsString());
                or.setCurrency(ritem.get("currency").getAsString());
                or.setTime(ritem.get("time").getAsLong());

                JsonArray epr = ritem.getAsJsonArray("products");

                for(JsonElement pr : epr) {
                    JsonObject pitem = pr.getAsJsonObject();
                    Product p = new Product();

                    p.setId(pitem.get("id").getAsInt());
                    p.setReceiptId(pitem.get("receipt_id").getAsInt());
                    p.setName(pitem.get("name").getAsString());
                    p.setPrice(pitem.get("price").getAsFloat());
                    p.setCnt(pitem.get("cnt").getAsFloat());
                    p.setImgPath(pitem.get("img").getAsString());
                    p.setTime(pitem.get("time").getAsLong());

                    or.getProducts().add(p);
                }

                Receipt.getList().add(or);
            }

            fin.close();
        } catch (Exception e) {
            Log.w("Settings Saver", "Nothing to load.");
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
