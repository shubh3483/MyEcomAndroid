package com.example.myecom.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.models.Cart;
import com.example.models.Product;
import com.example.models.ProductType;
import com.example.myecom.controllers.binders.ProductBinder;
import com.example.myecom.databinding.ItemVbProductBinding;
import com.example.myecom.databinding.ItemWbProductBinding;
import com.example.myecom.controllers.holders.VBViewHolder;
import com.example.myecom.controllers.holders.WBViewHolder;

import java.util.List;

/**
 * Represents adapter for the cart items
 */
public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * Context of the main activity
     */
    private final Context context;

    /**
     * List of the products
     */
    private final List<Product> products;

    /**
     * Binder object to bind the list item
     */
    private final ProductBinder productBinder;

    /**
     * Constructor to initialize the adapter
     * @param context context of the main activity
     * @param products list of products available
     * @param cart user cart
     * @param listener listener for callbacks
     */
    public ProductsAdapter(Context context, List<Product> products, Cart cart, AdapterCallbacksListener listener){
        this.context = context;
        this.products = products;

        // Initializing the binder
        productBinder = new ProductBinder(context, cart, listener);
    }

    @Override
    public int getItemViewType(int position) {
        return products.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Checking the type and creating view holder accordingly
        if(viewType == ProductType.TYPE_WEIGHT_BASED_PRODUCT){
            ItemWbProductBinding binding = ItemWbProductBinding.inflate(LayoutInflater.from(context), parent, false);
            return new WBViewHolder(binding);
        } else {
            ItemVbProductBinding binding = ItemVbProductBinding.inflate(LayoutInflater.from(context), parent, false);
            return new VBViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Getting the product from the list
        Product product = products.get(position);

        // Checking the view holder type and binding accordingly
        if(holder instanceof WBViewHolder){
            productBinder.bind(((WBViewHolder) holder).binding, product);
        } else {
            productBinder.bind(((VBViewHolder) holder).binding, product);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}