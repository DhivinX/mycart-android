/*
 * Created by Dominik Szaradowski on 24.05.19 12:15
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Product {
    private long id = -1;
    private long id_receipt = -1;
    private String name;
    private float price;
    private float cnt;
    private String img;
    private int type = 0;
    private long time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIdReceipt(long id) {
        this.id_receipt = id;
    }

    public long getId_receipt() {
        return id_receipt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getCnt() {
        return cnt;
    }

    public void setCnt(float cnt) {
        this.cnt = cnt;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getVal(){
        return getCnt() * getPrice();
    }

    public Bitmap getImg(Context ctx) {
        if(this.img == null) return null;

        File f = new File(ctx.getFilesDir(), this.img);
        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public void setImg(Context ctx, Bitmap img) {
        try {
            this.img = "product_img_rc" + id_receipt + "_t"+System.currentTimeMillis()+".jpg";
            File f = new File(ctx.getFilesDir(), this.img);

            FileOutputStream out = new FileOutputStream(f);
            img.compress(Bitmap.CompressFormat.JPEG, 60, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void delImg(Context ctx) {
        if(this.img == null) return;

        try {
            File f = new File(ctx.getFilesDir(), this.img);
            f.delete();
            this.img = null;
        }catch (Exception e){
            this.img = null;
        }
    }

    public void setImgPath(String path) {
        this.img = path;
    }

    public String getImgPath() {
        return img;
    }
}
