package com.example.brianalmanzar.myinventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.example.brianalmanzar.myinventoryapp.data.InventoryContract;

import java.util.ArrayList;

public class ProductUtil {

    /**
     * Takes a cursor as an argument then it returns a list of objects. If cursor is null, it returns null.
     * @param cursor <Cursor> : The data structure that contains the data from the database
     * @return ArrayList<Prodcut> : A list of products - null if cursor is empty
     * */
    public static ArrayList<Product> extractInformationAndConvertedFrom(Cursor cursor){
        ArrayList<Product> products = new ArrayList<>();

        if(cursor == null || cursor.getCount() <= 0){
            return null;
        }

        // Move to first column
        boolean notNull = cursor.moveToNext();

        while (notNull){

            // Gets the index column of each value from the cursor
            int productIDIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry._ID);
            int productNameIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_NAME);
            int productPriceIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_PRICE);
            int productQuantityIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_QUANTITY);
            int productSupplierNameIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME);
            int productSupplierPhoneNumberIndex = cursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE_NUMBER);

            // Extract the data from the cursor using the index above
            Long productId = cursor.getLong(productIDIndex);
            String productName = cursor.getString(productNameIndex);
            float productPrice = cursor.getFloat(productPriceIndex);
            int productQuantity = cursor.getInt(productQuantityIndex);
            String productSupplierName = cursor.getString(productSupplierNameIndex);
            String productSupplierPhoneNumber = cursor.getString(productSupplierPhoneNumberIndex);

            products.add(new Product(productId, productName, productPrice, productQuantity, productSupplierName, productSupplierPhoneNumber));
            notNull = cursor.moveToNext();
        }

        Log.i("PRODUCT UTIL :: ", String.valueOf(products.size()));

        return products;
    }


    /**
     * Help convert an object from a Product class to a ContentValues class.
     * @param productToConvert : Object that is going to be used to get the data from.
     * @return <ContentValues> : The key/value pair data structure with the data from fields of the product object
     */
    public static ContentValues convertProductObjectToContentValues(Product productToConvert){

        ContentValues contentValues = new ContentValues();

        contentValues.put(InventoryContract.InventoryEntry.PRODUCT_NAME, productToConvert.getProductName());
        contentValues.put(InventoryContract.InventoryEntry.PRODUCT_PRICE, productToConvert.getProductPrice());
        contentValues.put(InventoryContract.InventoryEntry.PRODUCT_QUANTITY, productToConvert.getProductQuantity());
        contentValues.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME, productToConvert.getSupplierName());
        contentValues.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE_NUMBER, productToConvert.getSupplierPhoneNumber());

        return contentValues;
    }
}
