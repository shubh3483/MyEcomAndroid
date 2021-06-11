package com.example.myecom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.models.Cart;
import com.example.models.Product;
import com.example.myecom.controllers.AdapterCallbacksListener;
import com.example.myecom.controllers.ProductsAdapter;
import com.example.myecom.databinding.ActivityMainBinding;
import com.example.myecom.tmp.ProductsHelper;
import com.google.gson.Gson;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * Binding of the user interface
     */
    private ActivityMainBinding mainBinding;

    /**
     * List of products
     */
    private List<Product> products;

    /**
     * Cart of the items
     */
    private Cart cart;

    /**
     * Adapter for the recycler view
     */
    private ProductsAdapter adapter;

    /**
     * preferences to save data in shared manner
     */
    private SharedPreferences preferences;

    /**
     * Constant to save the data in shared preferences
     */
    private static final String KEY_CART = "Cart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        // Defining shared preferences
        preferences = getPreferences(MODE_PRIVATE);
        // Checking availability of the data present
        if (preferences.contains(KEY_CART)) {
            getDataFromSharedPrefs();
        }
        products = ProductsHelper.getProducts();

        // Setting the adapters
        setupAdapter();
    }

    /**
     * Setup the adapter to the recycler view
     */
    private void setupAdapter() {
        // Defining the listener
        AdapterCallbacksListener listener = new AdapterCallbacksListener() {
            @Override
            public void onCartUpdated(int itemPosition) {
                updateCartSummary();
                adapter.notifyItemChanged(itemPosition);
            }
        };

        // Defining the adapter and setting it to the list
        adapter = new ProductsAdapter(this, products, cart, listener);
        mainBinding.list.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.list.setAdapter(adapter);
    }

    /**
     * Update the summary for the shopping
     */
    public void updateCartSummary(){
        // Setting the new summary
        mainBinding.tvTotalItems.setText(cart.numberOfItems + " items");
        mainBinding.tvTotalAmount.setText("₹" + cart.totalAmount);
    }

    /**
     * To get the data from the shared preferences
     */
    public void getDataFromSharedPrefs(){
        // Getting json from preferences
        String json = preferences.getString(KEY_CART, "");
        // Converting json into cart
        cart = (new Gson()).fromJson(json, Cart.class);
        // Updating the summary
        updateCartSummary();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Making json of cart
        String json = (new Gson()).toJson(cart);

        // Saving to preferences
        preferences.edit()
                .putString(KEY_CART, json)
                .apply();
    }
}