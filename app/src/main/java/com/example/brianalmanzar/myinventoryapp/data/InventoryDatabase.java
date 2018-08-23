package com.example.brianalmanzar.myinventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventoryDatabase extends SQLiteOpenHelper {

    /**
     * Database name to insert products
     */
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    public InventoryDatabase(Context contextRefence){
        super(contextRefence, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + InventoryContract.InventoryEntry.TABLE_NAME + "( " +
                InventoryContract.InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                InventoryContract.InventoryEntry.PRODUCT_NAME + " TEXT NOT NULL, " +
                InventoryContract.InventoryEntry.PRODUCT_PRICE + " REAL NOT NULL, " +
                InventoryContract.InventoryEntry.PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL, " +
                InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE_NUMBER + " TEXT " + ");";

        db.execSQL(CREATE_TABLE);
    }

    // No other new version imolemented so far.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
