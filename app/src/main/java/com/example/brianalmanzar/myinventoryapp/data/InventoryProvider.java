package com.example.brianalmanzar.myinventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class InventoryProvider extends ContentProvider {

    private static final int INVENTORY_MATCH = 100;
    private static final int INVENTORY_ITEM_ID_MATCH = 101;
    /**
     *  URI Matcher
     **/
    public static UriMatcher inventoryUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        inventoryUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_TO_DATABASE, INVENTORY_MATCH);
        inventoryUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_TO_DATABASE + "/#", INVENTORY_ITEM_ID_MATCH);
    }

    // Database access variable
    private InventoryDatabase accessToInventoryDatabase;

    public static String[] INVENTORY_DB_COLUMNS =  {InventoryContract.InventoryEntry._ID,
            InventoryContract.InventoryEntry.PRODUCT_NAME,
            InventoryContract.InventoryEntry.PRODUCT_PRICE,
            InventoryContract.InventoryEntry.PRODUCT_QUANTITY,
            InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME,
            InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE_NUMBER};

    @Override
    public boolean onCreate() {
        accessToInventoryDatabase = new InventoryDatabase(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        int match = inventoryUriMatcher.match(uri);

        SQLiteDatabase database = accessToInventoryDatabase.getReadableDatabase();

        Cursor cursor = null;

        switch (match){
            case INVENTORY_MATCH:
                Log.v("FROM DATABASE QUERY ::", "IT WORKS!");
                 cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INVENTORY_ITEM_ID_MATCH:

                  String newSelection = InventoryContract.InventoryEntry._ID + "=?";
                  String[] newSelectionArgs = {String.valueOf(ContentUris.parseId(uri))};

                  cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, newSelection, newSelectionArgs, null, null, sortOrder);
                break;
                default:
                    throw new IllegalArgumentException("An error occurred while reading product from database");
        }

        if(cursor != null){
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        Log.i("CURSOR LENGTH ", String.valueOf(cursor.getCount()));
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        int match = inventoryUriMatcher.match(uri);

        switch (match){
            case INVENTORY_MATCH:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ITEM_ID_MATCH:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
                default:
                    throw new IllegalStateException("Unknown URI " + uri.toString() + " for match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        long rowInsertedID;

        int match = inventoryUriMatcher.match(uri);

        switch (match){
            case INVENTORY_MATCH:
                rowInsertedID = insertIntoInventoryDatabase(uri, values);
                break;
                default:
                    throw new IllegalArgumentException("Error while inserting prodcut into the database");
        }
        return ContentUris.withAppendedId(uri, rowInsertedID);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = inventoryUriMatcher.match(uri);

        SQLiteDatabase databaseToDeleteProductsFrom = accessToInventoryDatabase.getWritableDatabase();

        int rowsDeleted = 0;

        switch (match){
            case INVENTORY_MATCH:
                rowsDeleted = databaseToDeleteProductsFrom.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ITEM_ID_MATCH:
                String newSelection = InventoryContract.InventoryEntry._ID + "=?";
                String[] newSelectionArgs = {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = databaseToDeleteProductsFrom.delete(InventoryContract.InventoryEntry.TABLE_NAME, newSelection, newSelectionArgs);
                break;
                default:
                    throw new IllegalArgumentException("Wrong URI provided " + uri.toString());
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = inventoryUriMatcher.match(uri);

        switch (match){
            case INVENTORY_MATCH:
                return updateProduct(uri, values, selection, selectionArgs);

            case INVENTORY_ITEM_ID_MATCH:
                String newSelection = InventoryContract.InventoryEntry._ID + "=?";
                String[] newSelectionArgs = {String.valueOf(ContentUris.parseId(uri))};

                return updateProduct(uri,values, newSelection, newSelectionArgs);

                default:
                    throw new IllegalArgumentException("Unknown uri to update product :: " + uri.toString());
        }
    }

    private long insertIntoInventoryDatabase(Uri uri, ContentValues productToInsert){
        long rowInsertedID;

        String productName = productToInsert.getAsString(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME);
        if(productName == null){
            throw new IllegalArgumentException("Product required a name");
        }

        Integer productQuantity = productToInsert.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_QUANTITY);
        if(productQuantity == null || productQuantity <= 0){
            throw new IllegalArgumentException("There should be at least 1 product on the stock to be added to the database");
        }

        String productSupplier = productToInsert.getAsString(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME);
        if(productSupplier == null){
            throw new IllegalArgumentException("Supplier name must be provided");
        }

        SQLiteDatabase databaseToInsertProduct = accessToInventoryDatabase.getWritableDatabase();

        getContext().getContentResolver().notifyChange(uri, null);
        rowInsertedID = databaseToInsertProduct.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, productToInsert);

        return rowInsertedID;
    }


    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){

        if(contentValues.containsKey(InventoryContract.InventoryEntry.PRODUCT_NAME)){
            String productName = contentValues.getAsString(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME);
            if(productName == null){
                throw new IllegalArgumentException("Product required a name");
            }
        }

        if(contentValues.containsKey(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME)){
            String productSupplier = contentValues.getAsString(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME);
            if(productSupplier == null){
                throw new IllegalArgumentException("Supplier name must be provided");
            }
        }

        if(contentValues.size() == 0){
            return 0;
        }

        SQLiteDatabase databaseToUpdateProduct = accessToInventoryDatabase.getWritableDatabase();

        getContext().getContentResolver().notifyChange(uri, null);

        return databaseToUpdateProduct.update(InventoryContract.InventoryEntry.TABLE_NAME, contentValues, selection, selectionArgs);
    }
}
