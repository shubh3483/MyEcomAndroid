package com.example.myecom.controllers.binders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.models.Cart;
import com.example.models.Product;
import com.example.models.Variant;
import com.example.myecom.controllers.AdapterCallbacksListener;
import com.example.myecom.MainActivity;
import com.example.myecom.R;
import com.example.myecom.databinding.ChipItemVariantBinding;
import com.example.myecom.databinding.DialogVariantPickerBinding;
import com.example.myecom.databinding.DialogWeightPickerBinding;
import com.example.myecom.databinding.ItemVbProductBinding;
import com.example.myecom.databinding.ItemWbProductBinding;
import com.example.myecom.databinding.ListItemVariantBinding;
import com.example.myecom.dialogs.VariantsQtyPickerDialog;
import com.example.myecom.dialogs.WeightPickerDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

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
     * Dialog for the product
     */
    private AlertDialog dialog;

    private final WeightPickerDialog weightPickerDialog;
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
        wbProductBinding.addBtn.setOnClickListener(view -> setupDialog(wbProductBinding, product));
        wbProductBinding.wbEditBtn.setOnClickListener(view -> setupDialog(wbProductBinding, product));
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

    // Increasing/Decreasing quantity methods

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
        setupDialog(vbProductBinding, product);
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
        setupDialog(vbProductBinding, product);
    }

    // Generating dialog methods

    /**
     * To setup the dialog to add or edit the product
     * @param wbProductBinding binding of the item in the recycler view
     * @param product product to be added or edited
     */
    private void setupDialog(ItemWbProductBinding wbProductBinding, Product product) {
        // Setup the dialog and showing it
        DialogWeightPickerBinding wbDialogBinding = DialogWeightPickerBinding.inflate(inflater);
        dialog = new MaterialAlertDialogBuilder(mContext, R.style.CustomDialogTheme)
                .setView(wbDialogBinding.getRoot())
                .setCancelable(false)
                .show();

        // Setting name of the product
        wbDialogBinding.titleProduct.setText(product.name);

        // Setup values for the pickers
        ArrayList<String> kilogramValues = new ArrayList<>();
        ArrayList<String> gramValues = new ArrayList<>();
        setupNumberPickers(wbDialogBinding, product, kilogramValues, gramValues);

        // Setup buttons
        wbDialogBinding.btnSave.setOnClickListener(v -> {
            // Get selected quantity from number picker
            String kg = kilogramValues.get(wbDialogBinding.pickerKg.getValue()).replace("kg", ""),
                    gm = gramValues.get(wbDialogBinding.pickerG.getValue()).replace("g", "");

            // Adding the item in the cart
            float quantity = Float.parseFloat(kg) + Float.parseFloat(gm) / 1000;

            // Checking quantity entered
            if (quantity < product.minQty) {
                Toast.makeText(mContext, "Minimum " + product.minQty + " kg needs to be selected", Toast.LENGTH_SHORT).show();
                return;
            }
            save(wbProductBinding, product, quantity);
        });
        wbDialogBinding.btnRemove.setOnClickListener(v -> remove(wbProductBinding, product));
    }

    /**
     * To setup the dialog to add or edit the product
     * @param vbProductBinding binding of the item in the recycler view
     * @param product product to be added or edited
     */
    private void setupDialog(ItemVbProductBinding vbProductBinding, Product product) {
        DialogVariantPickerBinding vbDialogBinding = DialogVariantPickerBinding.inflate(inflater);
        dialog = new MaterialAlertDialogBuilder(mContext, R.style.CustomDialogTheme)
                .setView(vbDialogBinding.getRoot())
                .setCancelable(false)
                .show();

        //Set up variants quantity picker dialog
        vbDialogBinding.titleProduct.setText(product.name);

        // Set the variants list and get the list of bindings of the variants
        List<ListItemVariantBinding> itemVariants = setupVariants(vbDialogBinding, product);


        vbDialogBinding.btnSave.setOnClickListener(v -> save(vbProductBinding, product, itemVariants));

        vbDialogBinding.btnRemoveAll.setOnClickListener(v -> remove(vbProductBinding, product));
    }

    // Saving methods

    /**
     * To save the changes made to an product
     * @param wbProductBinding binding of the item in the recycler view
     * @param product product to be saved
     * @param quantity quantity of the product to be saved
     */
    private void save(ItemWbProductBinding wbProductBinding, Product product, float quantity) {
        // Add the item into the cart
        mCart.add(product, quantity);

        // Update the item UI
        wbProductBinding.nonZeroQtyGroup.setVisibility(View.VISIBLE);
        wbProductBinding.wbQtyTV.setText(String.format("%.2fkg", quantity));
        wbProductBinding.addBtn.setVisibility(View.INVISIBLE);

        // Callback for the item change
        mListener.onCartUpdated();
        // Finish the dialog
        dialog.dismiss();
    }

    /**
     * To save the changes made to an product
     * @param vbProductBinding binding of the item in the recycler view
     * @param product product to be saved
     * @param itemVariants list of variants of the product
     */
    private void save(ItemVbProductBinding vbProductBinding, Product product, List<ListItemVariantBinding> itemVariants) {
        // Firstly we have to remove all variants from the cart
        mCart.removeAllVariantsOfVariantBasedProduct(product);

        // Total quantity of all the variants of the product
        int quantity = 0;
        // Add product variants to cart according to selected quantity
        for (int i = 0; i < product.variants.size(); i++){
            ListItemVariantBinding listItemVariant = itemVariants.get(i);
            quantity += Integer.parseInt(listItemVariant.tvQty.getText().toString());

            // Add the particular variant continuously
            for (int j = 0; j < Integer.parseInt(listItemVariant.tvQty.getText().toString()); j++){
                mCart.add(product, product.variants.get(i));
            }
        }

        // Update data in variant based binding
        if (quantity == 0) {
            vbProductBinding.vbNonZeroQtyGrp.setVisibility(View.GONE);
        } else {
            vbProductBinding.vbNonZeroQtyGrp.setVisibility(View.VISIBLE);
        }

        // Set the quantity in the text field
        vbProductBinding.vbQtyTV.setText(String.valueOf(quantity));

        //Callback to update cart
        mListener.onCartUpdated();
        dialog.dismiss();
    }

    // Removing product completely methods

    /**
     * Remove the product from the cart completely
     * @param wbProductBinding binding of the item in the recycler view
     * @param product product to be removed
     */
    private void remove(ItemWbProductBinding wbProductBinding, Product product) {
        // If item not in cart then just close the dialog
        if (!mCart.cartItems.containsKey(product.name)) {
            dialog.dismiss();
            return;
        }

        // Removing the item from the cart
        mCart.remove(product);

        // Update the binding for the product
        checkWBProductInCart(wbProductBinding, product);

        // Callback for the item change
        mListener.onCartUpdated();
        dialog.dismiss();
    }

    /**
     * Remove all variants of the product from the cart completely
     * @param vbProductBinding binding of the item in the recycler view
     * @param product product to be removed
     */
    private void remove(ItemVbProductBinding vbProductBinding, Product product) {
        // Remove all variants from cart
        mCart.removeAllVariantsOfVariantBasedProduct(product);

        // Update UI of recycler view
        vbProductBinding.vbNonZeroQtyGrp.setVisibility(View.GONE);

        // Callback to update cart
        mListener.onCartUpdated();
        dialog.dismiss();
    }

    // Utility methods

    /**
     * Setup number picker for the weight based products
     * @param wbDialogBinding binding of the item in the recycler view
     * @param product product for which the number picker is to be saved
     * @param kilogramValues values to be set for kilograms
     * @param gramValues values to be set for grams
     */
    private void setupNumberPickers(DialogWeightPickerBinding wbDialogBinding, Product product, ArrayList<String> kilogramValues, ArrayList<String> gramValues) {
        // Setting Number Picker for kilograms
        int minIndex = 0;
        for (int i = 0; i <= 10; i++) {
            if (i < (int) product.minQty) {
                minIndex++;
            }
            kilogramValues.add(i + "kg");
        }
        wbDialogBinding.pickerKg.setMaxValue(10);
        wbDialogBinding.pickerKg.setDisplayedValues((String[]) kilogramValues.subList(4, 9).toArray());

        // Setting Number Picker for grams
        for (int i = 0; i < 20; i++) {
            gramValues.add(i * 50 + "g");
        }
        wbDialogBinding.pickerG.setMaxValue(19);
        wbDialogBinding.pickerG.setDisplayedValues((String[]) gramValues.subList(3, 9).toArray());

        // Checking for the product availability in the cart
        // If find then update the previous quantity
        if (mCart.cartItems.containsKey(product.name)){
            // Extracting the weight unit from the cart for the particular item
            // We have used 3 decimal places here to get the grams in 3 decimal places
            String[] quantityString = String.format("%.3f", mCart.cartItems.get(product.name).qty).split("\\.");
            int kilogram = Integer.parseInt(quantityString[0]),
                    gram = Integer.parseInt(quantityString[1]);

            // Setting the values to the picker
            wbDialogBinding.pickerKg.setValue(kilogram);
            wbDialogBinding.pickerG.setValue(gram/50);
        }
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
            ListItemVariantBinding listItemVariant = ListItemVariantBinding.inflate(inflater);
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
}