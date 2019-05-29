/*
 * Created by Dominik Szaradowski on 28.05.19 09:08
 * Copyright (c) 2019 . All rights reserved.
 * Website: https://www.szaradowski.pl
 */

package pl.szaradowski.mycart.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBManager extends SQLiteOpenHelper {
    public static int ACTION_INSERT = 0;
    public static int ACTION_UPDATE = 1;
    public static int ACTION_DELETE = 2;

    private Context ctx;

    public DBManager(Context context){
        super(context, "user_database.db", null, 1);

        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE table receipts(id INTEGER primary key autoincrement, name TEXT, img TEXT, time INTEGER)");
        db.execSQL("CREATE table products(id INTEGER primary key autoincrement, id_receipt INTEGER, name TEXT, img TEXT, type INTEGER, price REAL, cnt REAL, time INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /////////////////////////////////////////////////////// Receipts table
    public long setReceipt(Receipt receipt, int action){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();

        v.put("name", receipt.getName());
        v.put("time", receipt.getTime());
        v.put("img", receipt.getImgPath());

        if(action == ACTION_INSERT){
            return db.insert("receipts", null, v);
        }else if(action == ACTION_DELETE){
            for(Product p : getProductsList(receipt.getId())) p.delImg(ctx);

            db.delete("products", "id_receipt = ?", new String[]{
                    Long.toString(receipt.getId())
            });

            return db.delete("receipts", "id = ?", new String[]{
                    Long.toString(receipt.getId())
            });
        }else{
            return db.update("receipts", v, "id = ?", new String[]{
                    Long.toString(receipt.getId())
            });
        }
    }

    public Receipt getReceiptById(long id){
        Receipt receipt = null;
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT id, name, img, time, pr.cnt, pr.val FROM receipts LEFT JOIN (SELECT id_receipt, COUNT(*) AS cnt, SUM(price * cnt) AS val FROM products GROUP BY products.id_receipt) AS pr ON pr.id_receipt = receipts.id WHERE id = ?", new String[]{
                Long.toString(id)
        });

        if(c.moveToNext()){
            receipt = new Receipt();
            receipt.setId(c.getLong(0));
            receipt.setName(c.getString(1));
            receipt.setImgPath(c.getString(2));
            receipt.setTime(c.getLong(3));
            receipt.setCnt(c.getInt(4));
            receipt.setVal(c.getLong(5));
        }

        c.close();
        return receipt;
    }

    public ArrayList<Receipt> getReceiptsList() {
        long to = System.currentTimeMillis();

        return this.getReceiptsList(0, to);
    }

    public ArrayList<Receipt> getReceiptsList(long time_from, long time_to){
        ArrayList<Receipt> receipts = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT id, name, img, time, pr.cnt, pr.val FROM receipts LEFT JOIN (SELECT id_receipt, COUNT(*) AS cnt, SUM(price * cnt) AS val FROM products GROUP BY products.id_receipt) AS pr ON pr.id_receipt = receipts.id WHERE time BETWEEN "+time_from+" AND "+time_to+" ORDER BY time DESC", null);

        while(c.moveToNext()){
            Receipt receipt = new Receipt();
            receipt.setId(c.getLong(0));
            receipt.setName(c.getString(1));
            receipt.setImgPath(c.getString(2));
            receipt.setTime(c.getLong(3));
            receipt.setCnt(c.getInt(4));
            receipt.setVal(c.getLong(5));

            receipts.add(receipt);
        }

        c.close();
        return receipts;
    }

    public int countAllReceipts(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) AS cnt FROM receipts ", null);
        c.moveToNext();

        return c.getInt(0);
    }

    /////////////////////////////////////////////////////// Products table
    public long setProduct(Product product, int action){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();

        v.put("id_receipt", product.getId_receipt());
        v.put("name", product.getName());
        v.put("price", product.getPrice());
        v.put("cnt", product.getCnt());
        v.put("img", product.getImgPath());
        v.put("type", product.getType());
        v.put("time", product.getTime());

        if(action == ACTION_INSERT){
            return db.insert("products", null, v);
        }else if(action == ACTION_DELETE){
            product.delImg(ctx);

            return db.delete("products", "id = ?", new String[]{
                    Long.toString(product.getId())
            });
        }else{
            return db.update("products", v, "id = ?", new String[]{
                    Long.toString(product.getId())
            });
        }
    }

    public Product getProductById(long id){
        Product product = null;
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT id, id_receipt, name, price, cnt, img, type, time FROM products WHERE id = ?", new String[]{
                Long.toString(id)
        });

        if(c.moveToNext()){
            product = new Product();
            product.setId(c.getLong(0));
            product.setIdReceipt(c.getLong(1));
            product.setName(c.getString(2));
            product.setPrice(c.getFloat(3));
            product.setCnt(c.getFloat(4));
            product.setImgPath(c.getString(5));
            product.setType(c.getInt(6));
            product.setTime(c.getLong(7));
        }

        c.close();
        return product;
    }

    public ArrayList<Product> getProductsList(long id_receipt){
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT id, id_receipt, name, price, cnt, img, type, time FROM products WHERE id_receipt = ?", new String[]{
                Long.toString(id_receipt)
        });

        while(c.moveToNext()){
            Product product = new Product();
            product.setId(c.getLong(0));
            product.setIdReceipt(c.getLong(1));
            product.setName(c.getString(2));
            product.setPrice(c.getFloat(3));
            product.setCnt(c.getFloat(4));
            product.setImgPath(c.getString(5));
            product.setType(c.getInt(6));
            product.setTime(c.getLong(7));

            products.add(product);
        }

        c.close();
        return products;
    }

    public Product findPreviousProductFromProduct(Product product){
        Product p = null;
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT id, id_receipt, name, price, cnt, img, type, time FROM products WHERE id_receipt != ? AND time < ? AND name LIKE ? ORDER BY time DESC", new String[]{
                Long.toString(product.getId_receipt()),
                Long.toString(product.getTime()),
                product.getName()
        });

        if(c.moveToNext()){
            p = new Product();
            p.setId(c.getLong(0));
            p.setIdReceipt(c.getLong(1));
            p.setName(c.getString(2));
            p.setPrice(c.getFloat(3));
            p.setCnt(c.getFloat(4));
            p.setImgPath(c.getString(5));
            p.setType(c.getInt(6));
            p.setTime(c.getLong(7));
        }

        c.close();
        return p;
    }
}
