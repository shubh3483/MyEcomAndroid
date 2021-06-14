package com.example.myecom.dialogs;

import android.content.Context;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.example.models.Cart;
import com.example.models.Product;
import com.example.myecom.MainActivity;
import com.example.myecom.R;
import com.example.myecom.databinding.DialogVariantPickerBinding;
import com.example.myecom.databinding.ListItemVariantBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class VariantsQtyPickerDialog {

    /**
     * Context of the main activity
     */
    private final Context mContext;

    /**
     * Cart of items
     */
    private final Cart mCart;

    /**
     * For the dialog binding
     */
    private final DialogVariantPickerBinding vbDialogBinding;

    /**
     * For the dialog appeared
     */
    private final AlertDialog dialog;

    public VariantsQtyPickerDialog(Context context, Cart cart){
        this.mContext = context;
        this.mCart = cart;
        this.vbDialogBinding = DialogVariantPickerBinding.inflate(((MainActivity) context).getLayoutInflater());
        this.dialog = new MaterialAlertDialogBuilder(context, R.style.CustomDialogTheme)
                .setView(vbDialogBinding.getRoot())
                .setCancelable(false)
                .create();
    }

    public void show(Product product, VariantsQtyPickerCompleteListener listener){
        dialog.show();

        //Set up variants quantity picker dialog
        vbDialogBinding.titleProduct.setText(product.name);

        // Set the variants list and get the list of bindings of the variants
        List<ListItemVariantBinding> itemVariants = setupVariants(vbDialogBinding, product);


        vbDialogBinding.btnSave.setOnClickListener(v -> save(product, itemVariants, listener));

        vbDialogBinding.btnRemoveAll.setOnClickListener(v -> {
            // Remove all variants from cart
            mCart.removeAllVariantsOfVariantBasedProduct(product);

            listener.onCompleted();
            dialog.dismiss();
        });
    }

    /**
     * Setup variants list for the variant based products
     * @param vbDialogBinding binding of the item in the recycler view
     * @param product product for which the number picker is to be saved
     */
    private List<ListItemVariantBinding> setupVariants(DialogVariantPickerBinding vbDialogBinding, Product product) {
        // Make a list of variants
        List<ListItemVariantBinding> itemVariants = new ArrayList<>();

        // Add variant items in the list
        for (int i = 0; i < product.variants.size(); i++){
            // Inflating the view
            ListItemVariantBinding listItemVariant = ListItemVariantBinding.inflate(((MainActivity) mContext).getLayoutInflater());
            float price = product.variants.get(i).price;
            listItemVariant.chip.setText(String.format("%s - ₹%.2f", product.variants.get(i).name, price));

            // Checking the availability of the particular variant in the cart
            // If true then update the cart accordingly
            if (mCart.cartItems.containsKey(product.name + " " + product.variants.get(i).name)){
                int quantity = (int) mCart.cartItems.get(product.name + " " + product.variants.get(i).name).qty;
                listItemVariant.tvQty.setText(String.valueOf(quantity));
                listItemVariant.nonZeroQtyGrp.setVisibility(View.VISIBLE);
            }

            // Adding the binding in the list
            itemVariants.add(listItemVariant);

            // Handling Increment button
            listItemVariant.btnInc.setOnClickListener(v -> {
                int quantity = Integer.parseInt(listItemVariant.tvQty.getText().toString());
                listItemVariant.tvQty.setText(String.valueOf(++quantity));
                listItemVariant.nonZeroQtyGrp.setVisibility(View.VISIBLE);
            });

            // Handling decrement button
            listItemVariant.btnDec.setOnClickListener(v -> {
                int quantity = Integer.parseInt(listItemVariant.tvQty.getText().toString());
                if (--quantity == 0) {
                    listItemVariant.nonZeroQtyGrp.setVisibility(View.GONE);
                }
                listItemVariant.tvQty.setText(String.valueOf(quantity));
            });

            // Adding the variant in the dialog
            vbDialogBinding.listVariants.addView(listItemVariant.getRoot());
        }

        return itemVariants;
    }

    /**
     * To save the changes made to an product
     * @param product product to be saved
     * @param itemVariants list of variants of the product
     * @param listener listener for the callbacks
     */
    private void save(Product product, List<ListItemVariantBinding> itemVariants, VariantsQtyPickerCompleteListener listener) {
        // Firstly we have to remove all variants from the cart
        mCart.removeAllVariantsOfVariantBasedProduct(product);

        // Add product variants to cart according to selected quantity
        for (int i = 0; i < product.variants.size(); i++){
            ListItemVariantBinding listItemVariant = itemVariants.get(i);

            // Add the particular variant continuously
            for (int j = 0; j < Integer.parseInt(listItemVariant.tvQty.getText().toString()); j++){
                mCart.add(product, product.variants.get(i));
            }
        }

        listener.onCompleted();
        dialog.dismiss();
    }

    /**
     * Represents listener for the dialog completion
     */
    public interface VariantsQtyPickerCompleteListener {

        /**
         * When dialog completes
         */
        void onCompleted();
    }

}
