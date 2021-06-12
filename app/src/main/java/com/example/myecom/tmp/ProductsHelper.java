package com.example.myecom.tmp;

import android.net.Uri;

import com.example.models.Product;
import com.example.models.Variant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents helper class to fetch products from web
 */
public class ProductsHelper {

    /**
     * To get the list of the products
     * @return list of the products
     */
    public static List<Product> getProducts(){

        List<Product> products = new ArrayList<>();

        // Adding some demo products
        products.add(new Product("Apple", Uri.parse("android.resource://com.example.myecom/drawable/apple").toString(), 1, 80));
        products.add(new Product("Kiwi", Uri.parse("android.resource://com.example.myecom/drawable/kiwi").toString(), new ArrayList<>(
                Arrays.asList(new Variant("500g", 90), new Variant("1kg", 150))
        )));
        products.add(new Product("Banana", Uri.parse("android.resource://com.example.myecom/drawable/banana").toString(),2,30));
        products.add(new Product("Surf Excel", Uri.parse("android.resource://com.example.myecom/drawable/surf_excel").toString(), new ArrayList<>(
                Arrays.asList(new Variant("1kg", 95), new Variant("2kg", 180), new Variant("5kg", 400))
        )));
        products.add(new Product("Milk", Uri.parse("android.resource://com.example.myecom/drawable/milk").toString(), new ArrayList<>(
                Collections.singletonList(new Variant("1kg", 50))
        )));

        return products;
    }
}
