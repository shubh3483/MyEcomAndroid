package com.example.myecom.controllers.binders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.models.Cart;
import com.example.models.Product;
import com.example.models.Variant;
import com.example.myecom.MainActivity;
import com.example.myecom.controllers.AdapterCallbacksListener;
import com.example.myecom.databinding.ChipItemVariantBinding;
import com.example.myecom.databinding.ItemVbProductBinding;
import com.example.myecom.databinding.ItemWbProductBinding;
import com.example.myecom.dialogs.VariantsQtyPickerDialog;
import com.example.myecom.dialogs.WeightPickerDialog;

/**
 * Represent binder for the product
 */
public class ProductBinder {
    /**
     * Context of the main activity
     */
    private final Context mContext;

    /**
     * Cart of items
     */
    private final Cart mCart;

    /**
     * Callbacks of for the dialogs
     */
    private final AdapterCallbacksListener mListener;

    /**
     * Inflate to inflate the objects
     */
    private final LayoutInflater inflater;

    /**
     * For weight picker dialog
     */
    private final WeightPickerDialog weightPickerDialog;

    /**
     * For variant picker dialog
     */
    private final VariantsQtyPickerDialog variantsQtyPickerDialog;

    /**
     * To initialize the object
     * @param context context of the main activity
     * @param cart cart of the items
     * @param listener listener for the callbacks of the dialogs
     */
    public ProductBinder(Context context, Cart cart, AdapterCallbacksListener listener) {
        this.mContext = context;
        this.mCart = cart;
        this.mListener = listener;
        this.inflater = ((MainActivity) context).getLayoutInflater();
        this.weightPickerDialog = new WeightPickerDialog(context, cart);
        this.variantsQtyPickerDialog = new VariantsQtyPickerDialog(context, cart);
    }

    // Binding methods

    /**
     * To bind weight based product
     * @param wbProductBinding binding for the item in the recycler view
     * @param product product to be bind
     */
    public void bind(ItemWbProductBinding wbProductBinding, Product product) {
        // Binding image, name and price of the product
        Glide.with(mContext)
                .asBitmap()
                .load(product.imageURL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        wbProductBinding.progressBar.setVisibility(View.INVISIBLE);
                        wbProductBinding.wbImageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        wbProductBinding.wbProductName.setText(product.name);
        wbProductBinding.wbProductName.setSelected(true);
        wbProductBinding.wbPrice.setText(String.format("₹%.2f/kg", product.pricePerKg));

        // To check if product is already in cart
        checkWBProductInCart(wbProductBinding, product);

        // Setup the button
        wbProductBinding.addBtn.setOnClickListener(view -> triggerDialog(wbProductBinding, product));
        wbProductBinding.wbEditBtn.setOnClickListener(view -> triggerDialog(wbProductBinding, product));
    }

    /**
     * To bind variant based product
     * @param vbProductBinding binding for the item in the recycler view
     * @param product product to be bind
     */
    public void bind(ItemVbProductBinding vbProductBinding, Product product) {
        // Binding image, name and price of the product
        Glide.with(mContext)
                .asBitmap()
                .load(product.imageURL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        vbProductBinding.progressBar.setVisibility(View.INVISIBLE);
                        vbProductBinding.vbImageView.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
        vbProductBinding.vbProductName.setText(product.name);
        vbProductBinding.vbProductName.setSelected(true);
        vbProductBinding.noOfVariantsTV.setText(product.variants.size() + " variants");

        // Inflating the variants
        // Checking for the variants available if only one variant then change the name of the item
        vbProductBinding.arrowBtn.setVisibility(View.VISIBLE);
        vbProductBinding.variantsGrp.removeAllViews();
        if (product.variants.size() == 1) {
            vbProductBinding.arrowBtn.setVisibility(View.GONE);
            vbProductBinding.vbProductName.setText(String.format("%s %s", vbProductBinding.vbProductName.getText(), product.variants.get(0).name));
            vbProductBinding.noOfVariantsTV.setText(String.format("₹%.2f", product.variants.get(0).price));
        } else if (product.variants.size() > 1) {
            for (Variant variant : product.variants) {
                ChipItemVariantBinding chipBinding = ChipItemVariantBinding.inflate(inflater);
                chipBinding.getRoot().setText(String.format("%s - ₹%.2f", variant.name, variant.price));
                vbProductBinding.variantsGrp.addView(chipBinding.getRoot());
            }
        }

        checkVBProductInCart(vbProductBinding, product);

        // Handling arrow buttons
        vbProductBinding.arrowBtn.setOnClickListener(v -> {
            // Checking the visibility of the chip group and change accordingly
            if (vbProductBinding.variantsGrp.getVisibility() == View.GONE) {
                vbProductBinding.variantsGrp.setVisibility(View.VISIBLE);
                vbProductBinding.arrowBtn.animate().rotation(180);
            } else {
                vbProductBinding.variantsGrp.setVisibility(View.GONE);
                vbProductBinding.arrowBtn.animate().rotation(0);
            }
        });

        vbProductBinding.addBtn.setOnClickListener(v -> increaseQuantity(vbProductBinding, product));

        // If only one variant then action directly
        vbProductBinding.minusBtn.setOnClickListener(v -> decreaseQuantity(vbProductBinding, product));
    }

    // Checking item in cart methods

    /**
     * To check the product available in the cart or not
     * @param wbProductBinding binding for the item in the recycler view
     * @param product product to be checked
     */
    private void checkWBProductInCart(ItemWbProductBinding wbProductBinding, Product product) {
        // Check for product availability in cart
        // If available then update the item accordingly
        if (mCart.cartItems.containsKey(product.name)){
            wbProductBinding.nonZeroQtyGroup.setVisibility(View.VISIBLE);
            wbProductBinding.addBtn.setVisibility(View.INVISIBLE);
            wbProductBinding.wbQtyTV.setText(String.format("%.2fkg", mCart.cartItems.get(product.name).qty));
        } else {
            wbProductBinding.nonZeroQtyGroup.setVisibility(View.INVISIBLE);
            wbProductBinding.addBtn.setVisibility(View.VISIBLE);
            wbProductBinding.wbQtyTV.setText("");
        }
    }

    /**
     * To check the product available in the cart or not
     * @param vbProductBinding binding for the item in the recycler view
     * @param product product to be checked
     */
    private void checkVBProductInCart(ItemVbProductBinding vbProductBinding, Product product) {
        // Update binding accordingly if product is already in the cart
        int quantity = 0;
        for (int i = 0; i < product.variants.size(); i++){
            if (mCart.cartItems.containsKey(product.name + " " + product.variants.get(i).name)){
                quantity += mCart.cartItems.get(product.name + " " + product.variants.get(i).name).qty;
            }
        }

        // Checking for the quantity
        // If greater than 0 the make the non zero group visible
        if (quantity > 0) {
            vbProductBinding.vbNonZeroQtyGrp.setVisibility(View.VISIBLE);
            vbProductBinding.vbQtyTV.setText(String.valueOf(quantity));
        } else {
            vbProductBinding.vbNonZeroQtyGrp.setVisibility(View.GONE);
            vbProductBinding.vbQtyTV.setText("0");
        }
    }

    // Increasing/Decreasing quantity methods for variant based product

    /**
     * Increment the quantity of the product by 1
     * @param vbProductBinding binding for the item in the recycler view
     * @param product product to be incremented
     */
    private void increaseQuantity(ItemVbProductBinding vbProductBinding, Product product) {
        // Check the variants of the product
        // If only one then directly increase the quantity otherwise show a dialog
        if (product.variants.size() == 1) {
            // Get the previous quantity
            int qty = Integer.parseInt(vbProductBinding.vbQtyTV.getText().toString());
            // Increase the quantity and set it
            vbProductBinding.vbQtyTV.setText(String.valueOf(++qty));
            // Increase the quantity of the variant in the cart
            mCart.add(product, product.variants.get(0));
            // Make the non zero group visible
            vbProductBinding.vbNonZeroQtyGrp.setVisibility(View.VISIBLE);
            mListener.onCartUpdated();
            return;
        }
        triggerDialog(vbProductBinding, product);
    }

    /**
     * Decrement the quantity of the product by 1
     * @param vbProductBinding binding for the item in the recycler view
     * @param product product to be decremented
     */
    private void decreaseQuantity(ItemVbProductBinding vbProductBinding, Product product) {
        // Check the variants of the product
        // If only one then directly decrease the quantity otherwise show a dialog
        if (product.variants.size() == 1) {
            // Get the previous quantity
            int qty = Integer.parseInt(vbProductBinding.vbQtyTV.getText().toString());
            // Decrease the quantity and set it
            vbProductBinding.vbQtyTV.setText(String.valueOf(--qty));
            // Increase the quantity of the variant in the cart
            mCart.decrement(product, product.variants.get(0));
            if (qty == 0) {
                // Make the non zero group visible
                vbProductBinding.vbNonZeroQtyGrp.setVisibility(View.GONE);
            }
            mListener.onCartUpdated();
            return;
        }
        triggerDialog(vbProductBinding, product);
    }

    // Triggering dialog methods

    /**
     * To setup the dialog to add or edit the product
     * @param wbProductBinding binding of the item in the recycler view
     * @param product product to be added or edited
     */
    private void triggerDialog(ItemWbProductBinding wbProductBinding, Product product) {
        weightPickerDialog.show(product, () -> {
            checkWBProductInCart(wbProductBinding, product);
            mListener.onCartUpdated();
        });
    }

    /**
     * To setup the dialog to add or edit the product
     * @param vbProductBinding binding of the item in the recycler view
     * @param product product to be added or edited
     */
    private void triggerDialog(ItemVbProductBinding vbProductBinding, Product product) {
        variantsQtyPickerDialog.show(product, () -> {
            checkVBProductInCart(vbProductBinding, product);
            mListener.onCartUpdated();
        });
    }
}