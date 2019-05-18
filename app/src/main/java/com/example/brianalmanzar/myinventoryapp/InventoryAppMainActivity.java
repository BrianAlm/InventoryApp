package com.example.brianalmanzar.myinventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.example.brianalmanzar.myinventoryapp.data.InventoryContract;
import com.example.brianalmanzar.myinventoryapp.data.InventoryProvider;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InventoryAppMainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private final int LOADERID = 1;

    //Reference to the empty TextView data message
    @BindView(R.id.no_data_message_id)
    TextView noDataTextView;

    @BindView(R.id.recycler_view_data_presenter_id)
    RecyclerView recyclerViewReference;

    @OnClick({R.id.add_floating_button_id})
    public void triggersTheAddProductActivity(View view){
        Intent addActivity = new Intent(this, AddAndRemoveProductActivity.class);
        startActivity(addActivity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_app_main);
        ButterKnife.bind(this);

        getLoaderManager().initLoader(LOADERID, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_activity_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuID = item.getItemId();

        switch (menuID){
            case R.id.delete_all_item:
                deleteEverythingFromDatabase();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteEverythingFromDatabase(){
        getContentResolver().delete(InventoryContract.CONTENT_URI, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == LOADERID){
            return new CursorLoader(getApplicationContext(),InventoryContract.CONTENT_URI, InventoryProvider.INVENTORY_DB_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data != null && data.getCount() > 0) {

            noDataTextView.setVisibility(View.GONE);
            recyclerViewReference.setVisibility(View.VISIBLE);

            // Extract the data from the cursor and convert it to to an array list
            ArrayList<Product> products = ProductUtil.extractInformationAndConvertedFrom(data);

            InventoryRecyclerViewAdapter adapter = new InventoryRecyclerViewAdapter(getApplicationContext(), products);

            recyclerViewReference.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

            recyclerViewReference.setAdapter(adapter);

            return;
        }

        recyclerViewReference.setVisibility(View.GONE);
        noDataTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
