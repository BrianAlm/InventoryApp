package com.example.brianalmanzar.myinventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.brianalmanzar.myinventoryapp.data.InventoryContract;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InventoryRecyclerViewAdapter extends RecyclerView.Adapter<InventoryRecyclerViewAdapter.InventoryViewHolder>{

    private ArrayList<Product> productArrayList;
    private Context context;

    /**
     *  The constructor takes two parameters to initialize :
     *  @param context <Context> : The context of where is going to be initialize, it is also used throughout the class
     *  @param listOfProducts <ArrayList> : A list of objects that populates the data the views need
     * */
    public InventoryRecyclerViewAdapter(Context context, ArrayList<Product> listOfProducts) {
        this.productArrayList = listOfProducts;
        this.context = context;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);

        InventoryViewHolder inventoryViewHolder = new InventoryViewHolder(viewInflated);

        return inventoryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final InventoryViewHolder holder,final int position) {

        int inStock = productArrayList.get(position).getProductQuantity();

        holder.productNameTextView.setText(productArrayList.get(position).getProductName());

        holder.productPriceTextView.setText((context.getResources().getString(R.string.dollar_sign) + String.valueOf(productArrayList.get(position).getProductPrice())));

        if(inStock == 0) {
            //Change the color and the message of the in stock label to Out of Stock
            holder.productStockAvailability.setTextColor(context.getResources().getColor(R.color.out_of_stock_color));
            holder.productStockAvailability.setText(context.getResources().getString(R.string.out_of_stock_item));

            // Change the appearance  of the buy button to makes it look like if it is disabled
            holder.buyItemButton.setText(context.getResources().getString(R.string.out_of_stock_item));
            holder.buyItemButton.setBackgroundColor(context.getResources().getColor(R.color.disabled_button_color));

        }else{
            holder.productStockAmount.setText((String.valueOf(inStock) + " " + context.getResources().getString(R.string.products_left)));
            holder.buyItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Gets the current position of the view that was pressed om the RecyclerView to extract the object on that position
                    int productPosition = holder.getAdapterPosition();

                    // Gets the ID of the object that was pressed
                    long productID = productArrayList.get(productPosition).getProductId();

                    //Append the ID to the address of the database to update the object that needs to be updated
                    Uri uri = ContentUris.withAppendedId(InventoryContract.CONTENT_URI, productID);

                    // ContentValues with the data that are going to be updated
                    ContentValues contentValuesToUpdate = ProductUtil.convertProductObjectToContentValues(productArrayList.get(position));
                    if(contentValuesToUpdate.containsKey(InventoryContract.InventoryEntry.PRODUCT_QUANTITY)){
                        // Gets the quantity of the current product being updated
                        Integer quantityToUpdate = contentValuesToUpdate.getAsInteger(InventoryContract.InventoryEntry.PRODUCT_QUANTITY);

                        quantityToUpdate -= 1;

                        // Removes the quantity key/value pair to be added later with the updated quantity
                        contentValuesToUpdate.remove(InventoryContract.InventoryEntry.PRODUCT_QUANTITY);

                        // Added the quantity key/value pair with the updated data
                        contentValuesToUpdate.put(InventoryContract.InventoryEntry.PRODUCT_QUANTITY, quantityToUpdate);
                    }

                    context.getContentResolver().update(uri, contentValuesToUpdate, null, null);
                }
            });
        }

        holder.supplierName.setText(productArrayList.get(position).getSupplierName());

        holder.supplierPhoneNumber.setText(productArrayList.get(position).getSupplierPhoneNumber());

        // Sets up an intent to allow user to call supply provider by touching on the number
        holder.supplierPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent phoneDialerIntent = new Intent(Intent.ACTION_DIAL);

                phoneDialerIntent.setData(Uri.parse("tel:" + productArrayList.get(position).getSupplierPhoneNumber()));

                context.startActivity(phoneDialerIntent);
            }
        });

        holder.editProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int productPressedToEditPosition = holder.getAdapterPosition();
                long productIDToEdit = productArrayList.get(productPressedToEditPosition).getProductId();

                // URI of the product to be updated
                Uri uri = ContentUris.withAppendedId(InventoryContract.CONTENT_URI, productIDToEdit);

                Intent editIntent = new Intent(context, AddAndRemoveProductActivity.class);

                editIntent.setData(uri);

                context.startActivity(editIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.productArrayList.size();
    }

    /**
     *  Views that the RecyclerView manage to display its data
     * */
    public static class InventoryViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.product_name_id)
        TextView productNameTextView;

        @BindView(R.id.product_price_id)
        TextView productPriceTextView;

        @BindView(R.id.stock_availability_id)
        TextView productStockAvailability;

        @BindView(R.id.stock_amount_id)
        TextView productStockAmount;

        @BindView(R.id.supplier_name_id)
        TextView supplierName;

        @BindView(R.id.supplier_phone_number)
        TextView supplierPhoneNumber;

        @BindView(R.id.buy_button_id)
        Button buyItemButton;

        @BindView(R.id.edit_product_button_id)
        TextView editProductButton;

        public InventoryViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

        }

    }
}
