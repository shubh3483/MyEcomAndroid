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

import java.util.ArrayList;
import java.util.List;


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
     * For kilogram values
     */
    private final List<String> KILOGRAM_VALUES = new ArrayList<>();

    /**
     * For gram values
     */
    private final List<String> GRAM_VALUES = new ArrayList<>();

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

        // Initializing the kilogram and gram values
        for (int i = 0; i <= 10; i++) {
            KILOGRAM_VALUES.add(i + "kg");
        }
        for (int i = 0; i < 20; i++) {
            GRAM_VALUES.add(i * 50 + "g");
        }
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
        setupNumberPickers(wbDialogBinding, product);

        // Setup buttons
        wbDialogBinding.btnSave.setOnClickListener(v -> save(product, listener));

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
     */
    private void setupNumberPickers(DialogWeightPickerBinding wbDialogBinding, Product product) {
        // Minimum quantity of product in string array
        String[] qtyString = String.format("%.3f", product.minQty).split("\\.");

        // Minimum value for kilogram number picker
        int minIndexKilogram = Integer.parseInt(qtyString[0]);

        // Extracting the array for number picker
        List<String> subKilogramValues = KILOGRAM_VALUES.subList(minIndexKilogram, 11);
        String[] kilogramArray = new String[subKilogramValues.size()];
        kilogramArray = subKilogramValues.toArray(kilogramArray);

        // Setting Number Picker for kilograms
        wbDialogBinding.pickerKg.setMaxValue(0);
        wbDialogBinding.pickerKg.setDisplayedValues(kilogramArray);
        wbDialogBinding.pickerKg.setMaxValue(kilogramArray.length - 1);

        wbDialogBinding.pickerKg.setOnValueChangedListener((picker, oldVal, newVal) -> {
            String[] grams = wbDialogBinding.pickerG.getDisplayedValues();
             String g = grams[wbDialogBinding.pickerG.getValue()];

            String[] kilograms = picker.getDisplayedValues();
            String kg = kilograms[picker.getValue()].replace("kg", "");

            String[] gramArray;

            if (Integer.parseInt(kg) >= ((int) product.minQty + 1)) {
                List<String> subGramValues = GRAM_VALUES.subList(0, 20);
                gramArray = new String[subGramValues.size()];
                gramArray = subGramValues.toArray(gramArray);

            } else {
                // Minimum value for gram number picker
                int minIndexGram = Integer.parseInt(qtyString[1])/50;

                // Extracting the array for number picker
                List<String> subGramValues = GRAM_VALUES.subList(minIndexGram, 20);
                gramArray = new String[subGramValues.size()];
                gramArray = subGramValues.toArray(gramArray);
            }
            setGramsNumberPicker(gramArray);

            for (int i = 0; i < gramArray.length; i++) {
                if (gramArray[i].equals(g)) {
                    wbDialogBinding.pickerG.setValue(i);
                }
            }
        });

        // Minimum value for gram number picker
        int minIndexGram = Integer.parseInt(qtyString[1])/50;

        // Extracting the array for number picker
        List<String> subGramValues = GRAM_VALUES.subList(minIndexGram, 20);
        String[] gramArray = new String[subGramValues.size()];
        gramArray = subGramValues.toArray(gramArray);

        // Setting Number Picker for grams
        setGramsNumberPicker(gramArray);

        // Checking for the product availability in the cart
        // If find then update the previous quantity
        if (mCart.cartItems.containsKey(product.name)){
            // We have used 3 decimal places here to get the grams in 3 decimal places
            String[] quantityString = String.format("%.3f", mCart.cartItems.get(product.name).qty).split("\\.");

            // Checking that the grams of amount are sufficient
            if (Integer.parseInt(quantityString[0]) > (int) product.minQty) {
                // We have to set the complete picker for the grams other we will not able to see grams below the minimum Quantity grams
                subGramValues = GRAM_VALUES.subList(0, 20);
                gramArray = new String[subGramValues.size()];
                gramArray = subGramValues.toArray(gramArray);

                // Setting Number Picker for grams
                setGramsNumberPicker(gramArray);
            }

            for (int i = 0; i < kilogramArray.length; i++) {
                if ((quantityString[0] + "kg").equals(kilogramArray[i])) {
                    wbDialogBinding.pickerKg.setValue(i);
                }
            }
            for (int i = 0; i < gramArray.length; i++) {
                if ((quantityString[1] + "g").equals(gramArray[i])) {
                    wbDialogBinding.pickerG.setValue(i);
                }
            }
        }
    }

    /**
     * Setting Number Picker for grams
     * @param gramArray array of grams
     */
    private void setGramsNumberPicker(String[] gramArray) {
        wbDialogBinding.pickerG.setMaxValue(0);
        wbDialogBinding.pickerG.setDisplayedValues(gramArray);
        wbDialogBinding.pickerG.setMaxValue(gramArray.length - 1);
    }

    /**
     * To save the changes made to an product
     * @param product product to be saved
     * @param listener listener for the callbacks
     */
    private void save(Product product, WeightPickerCompleteListener listener) {
        // Getting the array of displayed value in number pickers
        String[] kilogramValues = wbDialogBinding.pickerKg.getDisplayedValues();
        String[] gramValues = wbDialogBinding.pickerG.getDisplayedValues();

        // Get selected quantity from number picker
        String kg = kilogramValues[wbDialogBinding.pickerKg.getValue()].replace("kg", ""),
                gm = gramValues[wbDialogBinding.pickerG.getValue()].replace("g", "");


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
