package com.example.brianalmanzar.myinventoryapp.data;

import android.provider.BaseColumns;

public final class InventoryContract {

    private InventoryContract(){

    }

    public static final class InventoryEntry implements BaseColumns{

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
