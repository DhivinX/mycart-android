/*
 * Created by Dominik Szaradowski on 24.05.19 11:47
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Receipt {
    private int product_last_id;
    private int id;
    private String name;
    private String currency = Settings.currency;
    private ArrayList<Product> products = new ArrayList<>();
    private Date date;

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String name) {
        this.currency = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<Product> getProducts() {
        return products;
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
        products.add(p);

        return products;
    }

    public boolean removeProduct(Product p){
        int index = 0;

        for(Product item : products){
            if(item.getId() == p.getId()) {
                products.remove(index);
                return true;
            }

            index++;
        }

        return false;
    }

    // STATIC
    private static int last_id = 0;
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

    public static boolean removeById(int id){
        int index = 0;

        for(Receipt item : receipts){
            if(item.getId() == id) {
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

        Collections.sort(receipts, new Comparator<Receipt>() {
            @Override
            public int compare(Receipt o1, Receipt o2) {
                return o2.getId() - o1.getId();
            }
        });

        return item;
    }
}
