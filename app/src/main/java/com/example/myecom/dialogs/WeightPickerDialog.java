package com.example.myecom.dialogs;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.models.Cart;
import com.example.models.Product;
import com.example.myecom.MainActivity;
import com.example.myecom.R;
import com.example.myecom.databinding.DialogWeightPickerBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class WeightPickerDialog {

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
    private final DialogWeightPickerBinding wbDialogBinding;

    /**
     * For the dialog appeared
     */
    private final AlertDialog dialog;

    /**
     * To initialize object
     * @param context context of the main activity
     * @param cart cart of items
     */
    public WeightPickerDialog(Context context, Cart cart){
        this.mContext = context;
        this.mCart = cart;
        this.wbDialogBinding = DialogWeightPickerBinding.inflate(((MainActivity) context).getLayoutInflater());
        this.dialog = new MaterialAlertDialogBuilder(context, R.style.CustomDialogTheme)
                .setView(wbDialogBinding.getRoot())
                .setCancelable(false)
                .create();
    }

    /**
     * To show the dialog
     * @param product product for which the dialog is to be shown
     * @param listener listener for the callbacks of dialog completion
     */
    public void show(Product product, WeightPickerCompleteListener listener){
        dialog.show();

        // Setting name of the product
        wbDialogBinding.titleProduct.setText(product.name);

        // Setup values for the pickers
        String[] kilogramValues = new String[11];
        String[] gramValues = new String[20];
        setupNumberPickers(wbDialogBinding, product, kilogramValues, gramValues);

        // Setup buttons
        wbDialogBinding.btnSave.setOnClickListener(v -> save(product, kilogramValues, gramValues, listener));

        wbDialogBinding.btnRemove.setOnClickListener(v -> {
            // If item not in cart then just close the dialog
            if (!mCart.cartItems.containsKey(product.name)) {
                dialog.dismiss();
                return;
            }

            // Removing the item from the cart
            mCart.remove(product);

            // Callback for the item change
            listener.onCompleted();
            dialog.hide();
        });
    }

    /**
     * Setup number picker for the weight based products
     * @param wbDialogBinding binding of the item in the recycler view
     * @param product product for which the number picker is to be saved
     * @param kilogramValues values to be set for kilograms
     * @param gramValues values to be set for grams
     */
    private void setupNumberPickers(DialogWeightPickerBinding wbDialogBinding, Product product, String[] kilogramValues, String[] gramValues) {
        // Setting Number Picker for kilograms
        for (int i = 0; i <= 10; i++) {
            kilogramValues[i] = i + "kg";
        }
        wbDialogBinding.pickerKg.setMaxValue(10);
        wbDialogBinding.pickerKg.setDisplayedValues(kilogramValues);

        // Setting Number Picker for grams
        for (int i = 0; i < 20; i++) {
            gramValues[i] = i * 50 + "g";
        }
        wbDialogBinding.pickerG.setMaxValue(19);
        wbDialogBinding.pickerG.setDisplayedValues(gramValues);

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
     * To save the changes made to an product
     * @param product product to be saved
     * @param kilogramValues values of kilograms in number picker
     * @param gramValues values of grams in number picker
     * @param listener listener for the callbacks
     */
    private void save(Product product, String[] kilogramValues, String[] gramValues, WeightPickerCompleteListener listener) {
        // Get selected quantity from number picker
        String kg = kilogramValues[(wbDialogBinding.pickerKg.getValue())].replace("kg", ""),
                gm = gramValues[(wbDialogBinding.pickerG.getValue())].replace("g", "");

        // Adding the item in the cart
        float quantity = Float.parseFloat(kg) + Float.parseFloat(gm) / 1000;

        // Checking quantity entered
        if (quantity < product.minQty) {
            Toast.makeText(mContext, "Minimum " + product.minQty + " kg needs to be selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add the item into the cart
        mCart.add(product, quantity);

        // Giving callback and hiding the dialog
        listener.onCompleted();
        dialog.hide();
    }

    /**
     * Represents listener for the dialog completion
     */
    public interface WeightPickerCompleteListener{

        /**
         * When dialog completes
         */
        void onCompleted();
    }
}
