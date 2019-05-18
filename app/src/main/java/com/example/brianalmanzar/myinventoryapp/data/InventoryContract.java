package com.example.brianalmanzar.myinventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {

    // Authority name to use with the content provider
    public static String CONTENT_AUTHORITY = "com.example.brianalmanzar.myinventoryapp";
    private static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static String PATH_TO_DATABASE = "product";

    // Access point to the database from the content provider
    public static Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TO_DATABASE);

    private InventoryContract(){

    }

    public static final class InventoryEntry implements BaseColumns{

        /**
         * The MIME type for a list of producst.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + InventoryContract.CONTENT_AUTHORITY + "/" + InventoryContract.PATH_TO_DATABASE;

        /**
         * The MIME type for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + InventoryContract.CONTENT_AUTHORITY + "/" + InventoryContract.PATH_TO_DATABASE;

        /**
         *  Name of the database table to store the data
         */
        public static final String TABLE_NAME = "product";

        /**
         *  unique ID to identify each item
         *  type : INTEGER
         */
        public static final String _ID = BaseColumns._ID;

        /**
         *  database product name header (name column)
         *  type : TEXT
         */
        public static final String PRODUCT_NAME = "product_name";

        /**
         *  database product price header (price column)
         *  type : REAL (float)
         */
        public static final String PRODUCT_PRICE = "product_price";

        /**
         * database product quantity (quantity column)
         * type : INTEGER
         */
        public static final String PRODUCT_QUANTITY = "product_quantity";

        /**
         * database supplier name (supplier name column)
         * type : TEXT
         */
        public static final String PRODUCT_SUPPLIER_NAME = "product_supplier_name";

        /**
         * database supplier phone number (supplier phone number column)
         */
        public static final String PRODUCT_SUPPLIER_PHONE_NUMBER = "product_supplier_phone_number";
    }
}
