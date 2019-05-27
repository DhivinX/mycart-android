/*
 * Created by Dominik Szaradowski on 24.05.19 11:47
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.common;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import pl.szaradowski.mycart.R;

public class Receipt {
    private int product_last_id;
    private int id;
    private String name;
    private Currency currency = Utils.currency;
    private ArrayList<Product> products = new ArrayList<>();
    private long time;

    public int getProduct_last_id() {
        return product_last_id;
    }

    public void setProduct_last_id(int product_last_id) {
        this.product_last_id = product_last_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTimeString(Context ctx){
        String tstr;

        if(RichDateUtils.isToday(time)) tstr = RichDateUtils.getHHMM(time);
        else if(RichDateUtils.isYesterday(time)) tstr = ctx.getString(R.string.yesterday);
        else if(RichDateUtils.isLast5day(time)) {
            tstr = ctx.getString(ctx.getResources().getIdentifier("day_sm_" + RichDateUtils.getDayOfWeek(time), "string", ctx.getPackageName()));
        }
        else if(RichDateUtils.isThisYear(time)) {
            tstr = RichDateUtils.getDayNumber(time) +" " + ctx.getString(ctx.getResources().getIdentifier("month_sm_" + RichDateUtils.getMonthNumber(time), "string", ctx.getPackageName()));
        }else{
            tstr = RichDateUtils.getDayNumber(time) + " " + ctx.getString(ctx.getResources().getIdentifier("month_sm_" + RichDateUtils.getMonthNumber(time), "string", ctx.getPackageName()))+ " " + RichDateUtils.getYear(time);
        }

        return tstr;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public float getVal(){
        float sum = 0;

        for(Product p : getProducts()){
            sum += p.getVal();
        }

        return sum;
    }

    public Product getProduct(int id){
        for(Product item : products){
            if(item.getId() == id)
                return item;
        }

        return null;
    }

    public ArrayList<Product> addProduct(Product p){
        product_last_id++;

        p.setId(product_last_id);
        p.setReceiptId(this.getId());
        products.add(p);

        return products;
    }

    public boolean removeProduct(Context ctx, Product p){
        int index = 0;

        for(Product item : products){
            if(item.getId() == p.getId()) {
                File f = new File(ctx.getFilesDir(), item.getImgPath());
                f.delete();

                products.remove(index);
                return true;
            }

            index++;
        }

        return false;
    }

    public void clearProducts(Context ctx){
        int index = 0;

        for(Product item : products){
            if(item.getImgPath() != null) {
                File f = new File(ctx.getFilesDir(), item.getImgPath());
                f.delete();
            }
        }

        products.clear();
    }

    public JsonObject getJson(){
        JsonObject j = new JsonObject();

        j.addProperty("id", id);
        j.addProperty("product_last_id", product_last_id);
        j.addProperty("name", name);
        j.addProperty("time", time);

        JsonArray pr = new JsonArray();

        for(Product p : products){
            pr.add(p.getJson());
        }

        j.add("products", pr);

        return j;
    }


    // STATIC
    public static int last_id = 0;
    private static ArrayList<Receipt> receipts = new ArrayList<>();

    public static ArrayList<Receipt> getList(){
        return receipts;
    }

    public static Receipt getById(int id){
        for(Receipt item : receipts){
            if(item.getId() == id)
                return item;
        }

        return null;
    }

    public static Product getLastProductFrom(Product product){
        for(Receipt r : receipts){
            for(Product p : r.getProducts()){
                if(product.getTime() > p.getTime() && product.getName().equalsIgnoreCase(p.getName()) && product.getReceipt_id() != p.getReceipt_id()){
                    return p;
                }
            }
        }

        return null;
    }

    public static boolean removeById(Context ctx, int id){
        int index = 0;

        for(Receipt item : receipts){
            if(item.getId() == id) {
                item.clearProducts(ctx);
                receipts.remove(index);
                return true;
            }

            index++;
        }

        return false;
    }

    public static Receipt add(String name){
        last_id++;

        Receipt item = new Receipt();
        item.setName(name);
        item.setId(last_id);

        receipts.add(item);

        return item;
    }

    public static void sort(){
        Collections.sort(receipts, new Comparator<Receipt>() {
            @Override
            public int compare(Receipt o1, Receipt o2) {
                return o2.getTime() < o1.getTime() ? -1 : o1.getTime() > o2.getTime() ? 1 : 0;
            }
        });
    }

    public static JsonObject allToJson(){
        JsonObject j = new JsonObject();
        j.addProperty("last_id", last_id);

        JsonArray rc = new JsonArray();

        for(Receipt r : receipts){
            rc.add(r.getJson());
        }

        j.add("receipts", rc);

        return j;
    }
}
