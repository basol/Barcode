package com.example.basol.barcode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.TabLayout;

import java.net.IDN;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Basol on 10.12.2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Barcode";
    private static final String TABLE_NAME = "Items";
    private static final String ITEM_ID = "id";
    private static final String ITEM_NAME = "product_name";
    private static final String ITEM_COUNTRY = "shop_address";
    private static final String ITEM_CATEGORY = "category";
    private static final String ITEM_URL = "url";
    private static final String ITEM_DIMENSIONS = "dimesions";
    private static final String ITEM_SHOPNAME = "shop_name";
    private static final String ITEM_PRICE = "price";
    private static final String ITEM_CURRENCY = "currency";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String Product = "CREATE TABLE " + TABLE_NAME + "(" + ITEM_ID + " INTEGER PRIMARY KEY," + ITEM_NAME + " TEXT," + ITEM_COUNTRY + " TEXT," +
                ITEM_CATEGORY + " TEXT," + ITEM_URL + " TEXT," + ITEM_DIMENSIONS + " TEXT,"  + ITEM_SHOPNAME + " TEXT,"+ ITEM_PRICE + " TEXT,"  + ITEM_CURRENCY + " TEXT"  + ")";
        sqLiteDatabase.execSQL(Product);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    public void addItem(Item item)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ITEM_NAME, item.getProductName());
        values.put(ITEM_COUNTRY, item.getCountryCode());
        values.put(ITEM_CATEGORY, item.getCategory());
        values.put(ITEM_URL, item.getUrl());
        values.put(ITEM_DIMENSIONS, item.getDimensions());
        values.put(ITEM_SHOPNAME, item.getShopName());
        values.put(ITEM_PRICE, item.getPrice());
        values.put(ITEM_CURRENCY, item.getCurrency());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }


    public Item getShop (int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{ITEM_ID, ITEM_NAME, ITEM_COUNTRY, ITEM_CATEGORY, ITEM_URL, ITEM_DIMENSIONS,ITEM_SHOPNAME,ITEM_PRICE,ITEM_CURRENCY}, ITEM_ID  + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
        {
            cursor.moveToFirst();
        }


        Item item = new Item();
        item.setId(Integer.parseInt(cursor.getString(0)));
        item.setProductName(cursor.getString(1));
        item.setCountryCode(cursor.getString(2));
        item.setCategory(cursor.getString(3));
        item.setUrl(cursor.getString(4));
        item.setDimensions(cursor.getString(5));
        item.setShopName(cursor.getString(6));
        item.setPrice(cursor.getString(7));
        item.setCurrency(cursor.getString(8));

        return item;
    }

    public ArrayList<Item> getAllShops()
    {
        ArrayList<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
        {
            do
            {
                Item item = new Item();
                item.setId(Integer.parseInt(cursor.getString(0)));
                item.setProductName(cursor.getString(1));
                item.setCountryCode(cursor.getString(2));
                item.setCategory(cursor.getString(3));
                item.setUrl(cursor.getString(4));
                item.setDimensions(cursor.getString(5));
                item.setShopName(cursor.getString(6));
                item.setPrice(cursor.getString(7));
                item.setCurrency(cursor.getString(8));


                itemList.add(item);
            } while (cursor.moveToNext());
        }
        return itemList;
    }

    public void deleteShop(Item item)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, ITEM_ID + " = ?", new String[]{String.valueOf(item.getId())});
        db.close();
    }
}
