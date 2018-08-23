package com.example.brianalmanzar.myinventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.brianalmanzar.myinventoryapp.data.InventoryContract;
import com.example.brianalmanzar.myinventoryapp.data.InventoryDatabase;

public class InventoryAppMainActivity extends AppCompatActivity {

    private TextView referenceToTextViewToTestDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_app_main);

        //Reference to the TextView that is going to present tha data from the database for testing purpose
        referenceToTextViewToTestDatabase = findViewById(R.id.database_data_entries);

        //Button to trigger the insert() method to add data / testing purpose
        Button addDataButton = findViewById(R.id.add_data_button_id);

        //Button to trigger the readData() method / It also displays the data on the screen
        Button readDataButton = findViewById(R.id.read_data_button_id);

        // Setting the Event Listener for both buttons
        addDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert();
            }
        });

        readDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readData();
            }
        });
    }

    /**
     *  Creates an instance of the Invertory Database to insert dummy data to it
     */
    private void insert(){
        SQLiteDatabase databaseToInsertDataTo = new InventoryDatabase(this).getWritableDatabase();

        ContentValues dataToInsert = new ContentValues();

        dataToInsert.put(InventoryContract.InventoryEntry.PRODUCT_NAME, "Inteligent Vision Device");
        dataToInsert.put(InventoryContract.InventoryEntry.PRODUCT_PRICE, 25.98);
        dataToInsert.put(InventoryContract.InventoryEntry.PRODUCT_QUANTITY, 10);
        dataToInsert.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME, "B&R Inc");
        dataToInsert.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE_NUMBER, "123456789");

        databaseToInsertDataTo.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, dataToInsert);
    }

    /**
     * Instantiate the class to reference the database to read data from it.
     */
    private void readData(){
        SQLiteDatabase databaseToReadDataFrom = new InventoryDatabase(this).getReadableDatabase();

        // Columns that is going to be use to pull its data
        String[] columnsToPull = {InventoryContract.InventoryEntry.PRODUCT_NAME, InventoryContract.InventoryEntry.PRODUCT_PRICE,
                                  InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME};

        //Reference to the data tha comes back from the database
        Cursor dataCursor = databaseToReadDataFrom.query(InventoryContract.InventoryEntry.TABLE_NAME, columnsToPull, null, null, null, null, null);
        try {

            // If nothing comes back from the database, skip next lines of code and close the cursor reference
        if(dataCursor.getCount() > 0) {

            int indexOfProductNameColumn = dataCursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_NAME);
            int indexOfProductPrice = dataCursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_PRICE);
            int indexOfProductSupplierName = dataCursor.getColumnIndex(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME);

            // While there is data in the cursor, keep reading and append it to the view.
            while (dataCursor.moveToNext()) {
                String productName = dataCursor.getString(indexOfProductNameColumn);
                float productPrice = dataCursor.getFloat(indexOfProductPrice);
                String productSupplierName = dataCursor.getString(indexOfProductSupplierName);

                referenceToTextViewToTestDatabase.append(" " + productName + " - " + "$" + String.valueOf(productPrice) + " - " + "by: " + productSupplierName + "\n");
            }
        }
        }finally {
            dataCursor.close();
        }
    }
}
