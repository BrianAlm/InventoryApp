package com.example.brianalmanzar.myinventoryapp;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.content.Loader;
import android.content.CursorLoader;
import android.app.LoaderManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.brianalmanzar.myinventoryapp.data.InventoryContract;
import com.example.brianalmanzar.myinventoryapp.data.InventoryProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;


// TODO : Have to finnish the update UI on edit mode method and implement the LoaderManager methods

public class AddAndRemoveProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final int EDITLOADERID = 2;

    @BindView(R.id.edit_product_name_id)
    EditText productName;

    @BindView(R.id.edit_product_price_id)
    EditText productPrice;

    @BindView(R.id.edit_product_quantity_id)
    EditText productQuantity;

    @BindView(R.id.edit_product_supplier_name_id)
    EditText productSupplierName;

    @BindView(R.id.edit_product_supplier_phone_number_id)
    EditText productSupplierPhoneNumber;

    /**
     *  Button to increase and decrease quantity amount
     * */
    @OnClick({R.id.edit_product_increase_quantity, R.id.edit_product_decrease_quantity})
    public void adjustQuantity(View button){

        final String INCREASEQUANTITYTAG = "1";

        final String DECREASEQUANTITYTAG = "0";

        String tag = button.getTag().toString();

        switch (tag){
            case INCREASEQUANTITYTAG:
                Toast.makeText(getApplicationContext(), "Increased By One", Toast.LENGTH_SHORT).show();
                increaseProductQuantityFromEditTextView(this.productQuantity);
                break;

            case DECREASEQUANTITYTAG:
                decreaseProductQuantityFromEditTextView(this.productQuantity);
                Toast.makeText(getApplicationContext(), "Decreased By One", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // Property to keep track of the activity mode. (Edit Product Mode | Add Product Mode)
    private boolean isActivityOnEditMode = false;
    private Uri productToBeUpdatedIfEditModeIsTrue = null;
    private boolean productChange = false;

    @OnTouch({R.id.edit_product_name_id, R.id.edit_product_price_id, R.id.edit_product_quantity_id, R.id.edit_product_supplier_name_id, R.id.edit_product_supplier_phone_number_id})
    public boolean itemWasUpdated(View view, MotionEvent motionEvent){
        productChange = true;

        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_and_remove_product);
        ButterKnife.bind(this);

        Intent intentToExtractTheURI = getIntent();
        productToBeUpdatedIfEditModeIsTrue = intentToExtractTheURI.getData();

        if(productToBeUpdatedIfEditModeIsTrue == null){
            setTitle(getResources().getString(R.string.add_product));
        }else{
            isActivityOnEditMode = true;
            setTitle(getResources().getString(R.string.edit_product));
            getLoaderManager().initLoader(EDITLOADERID, null, this);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_and_remove_product_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();

        switch (menuItemId){
            case R.id.add_product_menu_id:
                boolean dataWasInsertedCorrectly = insertDataToDB();
                if(dataWasInsertedCorrectly) {
                    finish();
                }
                return true;

            case R.id.delete_product_menu_id:
                showDeleteConfirmationDialog();
                return true;

            case R.id.homeAsUp:
                if (!productChange) {
                    NavUtils.navigateUpFromSameTask(AddAndRemoveProductActivity.this);
                    return true;
                }
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(AddAndRemoveProductActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(productToBeUpdatedIfEditModeIsTrue == null){
            MenuItem deleteButton = menu.findItem(R.id.delete_product_menu_id);
            deleteButton.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     *
     * @param editText <EditText> : Increase the quantity of products on stocks by one
     */
    private void increaseProductQuantityFromEditTextView(EditText editText){

        if(!TextUtils.isEmpty(editText.getText().toString())) {
            int currentAmount = getIntegerValueFromEditText(editText);

            currentAmount += 1;

            editText.setText(String.valueOf(currentAmount));
        }
    }

    /**
     *
     * @param editText <EditText> : Decrease the quantity of products on stocks by one
     */
    private void decreaseProductQuantityFromEditTextView(EditText editText){

        if (!TextUtils.isEmpty(editText.getText().toString()) && getIntegerValueFromEditText(editText) > 0){
            int currentAmout = getIntegerValueFromEditText(editText);

            currentAmout -= 1;

            editText.setText(String.valueOf(currentAmout));
        }
    }

    /**
     *
     * @param editText <EditText> : EditText to extract the integer value from
     * @return <int> : The integer that is converted from the text stored on the editText
     */
    private int getIntegerValueFromEditText(EditText editText){
        return Integer.valueOf(editText.getText().toString());
    }

    private boolean insertDataToDB(){

        //Checks if any of the EditText strings is empty
        String productName;
        if(TextUtils.isEmpty(this.productName.getText().toString().trim())){
            Toast.makeText(getApplicationContext(), "Please Enter A Name For The Product", Toast.LENGTH_SHORT).show();
            return false;
        }

        float productPrice;
        if(TextUtils.isEmpty(this.productPrice.getText().toString().trim())){
            Toast.makeText(getApplicationContext(), "Please Enter The Price Of The Product", Toast.LENGTH_SHORT).show();
            return false;
        }

        int quantity;
        if(TextUtils.isEmpty(this.productQuantity.getText().toString().trim())){
            Toast.makeText(getApplicationContext(), "Enter A Number For The Quantity", Toast.LENGTH_SHORT).show();
            return false;
        }

        String supplierName;
        if(TextUtils.isEmpty(this.productSupplierName.getText().toString().trim())){
            Toast.makeText(getApplicationContext(), "Please Provide A Name For The Supplier", Toast.LENGTH_SHORT).show();
            return false;
        }

        String supplierPhoneNumber;
        if (TextUtils.isEmpty(this.productSupplierPhoneNumber.getText().toString().trim())){
            Toast.makeText(getApplicationContext(), "Please Provide Supplier Phone Number", Toast.LENGTH_SHORT).show();
            return false;
        }

            productName = this.productName.getText().toString().trim();

            supplierName = this.productSupplierName.getText().toString().trim();

            supplierPhoneNumber = this.productSupplierPhoneNumber.getText().toString().trim();

            // If the phone number is not equal to 10 digits, cancel the data insertion.
            if(!validatePhoneNumber(supplierPhoneNumber)){
                Toast.makeText(getApplicationContext(), "Phone number should contain 10 digits (Area code is included).", Toast.LENGTH_SHORT).show();
                return false;
            }

            productPrice = Float.valueOf(this.productPrice.getText().toString().trim());

            quantity = getIntegerValueFromEditText(this.productQuantity);


        ContentValues valuesToAdd = new ContentValues();

        valuesToAdd.put(InventoryContract.InventoryEntry.PRODUCT_NAME, productName);
        valuesToAdd.put(InventoryContract.InventoryEntry.PRODUCT_PRICE, productPrice);
        valuesToAdd.put(InventoryContract.InventoryEntry.PRODUCT_QUANTITY, quantity);
        valuesToAdd.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME, supplierName);
        valuesToAdd.put(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhoneNumber);

        Uri rowInserted = null;
        int rowsUpdated = 0;

        // If activity is not on edit mode then invoke the insert method, else is on edit mode - invoke tge update method
        if (!isActivityOnEditMode) {
            rowInserted = getContentResolver().insert(InventoryContract.CONTENT_URI, valuesToAdd);

            if(rowInserted == null){
                Toast.makeText(this, "Error while inserting the product", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Product was inserted with ID : " + ContentUris.parseId(rowInserted), Toast.LENGTH_SHORT).show();
            }

        }else{

            String selection = InventoryContract.InventoryEntry._ID + "=?";
            String[] selectionArgs = {String.valueOf(ContentUris.parseId(productToBeUpdatedIfEditModeIsTrue))};

            rowsUpdated = getContentResolver().update(InventoryContract.CONTENT_URI, valuesToAdd, selection, selectionArgs);

            if(rowsUpdated == 0){
                Toast.makeText(this, "Error while updating the product", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Product was successfully updated", Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }

    /**
     *  Only checks if the phone number as String contains all numbers
     * @param phoneNumber : The phone number to validate
     * @return : Whether or not the it only contains numbers
     */
    private boolean validatePhoneNumber(String phoneNumber){
        if(!phoneNumber.matches("\\d{10}")){
            return false;
        }

        return true;
    }

    // Perform a deletion from the inventory database - Deletes a product
    private void deleteProductFromInventory(){

        String selection = InventoryContract.InventoryEntry._ID + "=?";

        String[] selectionArgs = {String.valueOf(ContentUris.parseId(productToBeUpdatedIfEditModeIsTrue))};

        getContentResolver().delete(InventoryContract.CONTENT_URI, selection, selectionArgs);

        finish();
    }

    /**
     * Update the UI with the data of the current product being updated
     * @param productName : The name of the product to update the Product TextEdit
     * @param productPrice : The price of the product to update the Price TextEdit
     * @param productQuantity : Product quantity to update the Quantity TextEdit
     * @param supplierName : Supplier name to update the Supplier Name TextEdit
     * @param supplierPhoneNumber : Supplier phoner number to update the Phone Number TextEdit
     */
    private void updateUIDataForEditModeActivityWith(String productName, float productPrice, int productQuantity, String supplierName, String supplierPhoneNumber){
        this.productName.setText(productName);
        this.productPrice.setText(String.valueOf(productPrice));
        this.productQuantity.setText(String.valueOf(productQuantity));
        this.productSupplierName.setText(supplierName);
        this.productSupplierPhoneNumber.setText(supplierPhoneNumber);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        if(id == EDITLOADERID){
            return new CursorLoader(getApplicationContext(), productToBeUpdatedIfEditModeIsTrue, InventoryProvider.INVENTORY_DB_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if(data != null && data.getCount() > 0){
            //move cursor to first item on the list
            data.moveToFirst();

            // Extract the data from the cursor
            String productName = data.getString(data.getColumnIndexOrThrow(InventoryContract.InventoryEntry.PRODUCT_NAME));
            float productPrice = data.getFloat(data.getColumnIndexOrThrow(InventoryContract.InventoryEntry.PRODUCT_PRICE));
            int productQuantity = data.getInt(data.getColumnIndexOrThrow(InventoryContract.InventoryEntry.PRODUCT_QUANTITY));
            String supplierName = data.getString(data.getColumnIndexOrThrow(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_NAME));
            String supplierPhoneNumber = data.getString(data.getColumnIndexOrThrow(InventoryContract.InventoryEntry.PRODUCT_SUPPLIER_PHONE_NUMBER));

            updateUIDataForEditModeActivityWith(productName, productPrice, productQuantity, supplierName, supplierPhoneNumber);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    // Method to be call when a product is changed but not saved
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener dialogListener){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setMessage(R.string.unsaved_changes);

        dialogBuilder.setPositiveButton(R.string.discard, dialogListener);

        dialogBuilder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Discard the changes
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(R.string.delete_dialog_msg);
        dialogBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProductFromInventory();
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialogToShow = dialogBuilder.create();
        alertDialogToShow.show();
    }
}
