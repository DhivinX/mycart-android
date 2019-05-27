/*
 * Created by Dominik Szaradowski on 27.05.19 15:42
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.common;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Locale;

public class Currency {
    private String id;
    private String name;
    private String prefix;
    private String suffix;
    private int zeropad = 2;

    public Currency(String id, String name, String prefix, String suffix){
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public Currency(JsonObject j){
        this.id = j.get("id").getAsString();
        this.name = j.get("name").getAsString();
        this.prefix = j.get("prefix").getAsString();
        this.suffix = j.get("suffix").getAsString();
        this.zeropad = j.get("zeropad").getAsInt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getZeropad() {
        return zeropad;
    }

    public void setZeropad(int zeropad) {
        this.zeropad = zeropad;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String formatPrice(float price){
        String p = String.format(Locale.getDefault(), "%."+zeropad+"f", price);

        return getPrefix() + p + getSuffix();
    }

    public String format(float price){
        String p = String.format(Locale.getDefault(), "%."+zeropad+"f", price);

        return p;
    }

    public JsonObject getJson(){
        JsonObject j = new JsonObject();

        j.addProperty("id", this.id);
        j.addProperty("name", this.name);
        j.addProperty("prefix", this.prefix);
        j.addProperty("suffix", this.suffix);
        j.addProperty("zeropad", this.zeropad);

        return j;
    }

    private static ArrayList<Currency> currencies = new ArrayList<>();

    public static void addCurrency(Currency c){
        currencies.add(c);
    }

    public static ArrayList<Currency> getAll(){
        return currencies;
    }

    public static Currency getById(String id){
        for(Currency c : currencies){
            if(c.id.equals(id)) return c;
        }

        return null;
    }
}
